package com.mobdeve.s12.aiwear.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.adapters.WardrobeFragmentAdapter
import com.mobdeve.s12.aiwear.adapters.OutfitCanvasView
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.models.OutfitModel
import com.mobdeve.s12.aiwear.models.UserModel
import com.mobdeve.s12.aiwear.utils.FirebaseStorageHandler
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat

interface OnCanvasUpdateListener {
    fun updateCanvas(isChecked: Boolean, clothesItem: ClothesItem, bitmap: Bitmap)
}

class CreateOutfitActivity : AppCompatActivity(), OnCanvasUpdateListener {

    companion object {
        const val SELECTED_DATE_KEY = "selectedDate"
        const val USER_UUID_KEY = "outfitUserUUID"
        val DATE_FORMAT = SimpleDateFormat("EEE, MMM dd")
    }

    // for outfit canvas
    private lateinit var canvasView: OutfitCanvasView

    private val PICK_IMAGE_REQUEST = 1

    // for wardrobe selection
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: WardrobeFragmentAdapter
    private var outfitClothes = ArrayList<ClothesItem>()
    private lateinit var nextBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_outfit)

        val selectedDate = intent.getStringExtra(CreateOutfitActivity.SELECTED_DATE_KEY)
        val currentUser = intent.getStringExtra(CreateOutfitActivity.USER_UUID_KEY).toString()

        // Set header
        initializeHeader(selectedDate!!)
        initializeOutfitCanvas()
        initializeWardrobe()

        nextBtn.setOnClickListener {
            val outfitBitmap = canvasView.saveCanvasToBitmap()
            var outfitPath = ""

            FirebaseStorageHandler.uploadOutfitBitmap(outfitBitmap) { path ->
                outfitPath = path

                val newOutfit = OutfitModel(
                    "",
                    currentUser,
                    UserModel.DATE_FORMAT.parse(selectedDate)!!,
                    canvasView.getBitmaps(),
                    canvasView.saveCanvasToBitmap(),
                    outfitPath
                )

                newOutfit.outfit_id = runBlocking { FirestoreDatabaseHandler.addOutfit(newOutfit) }

                Toast.makeText(
                    this,
                    "Successfully created outfit! ${newOutfit.outfit_id}",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun initializeHeader(selectedDate: String) {
        val headerTv = findViewById<TextView>(R.id.settingsHeaderTv)
        val date = UserModel.DATE_FORMAT.parse(selectedDate)
        val formattedDate = CreateOutfitActivity.DATE_FORMAT.format(date)
        headerTv.text = "OOTD for ${formattedDate}"

        nextBtn = findViewById(R.id.nextBtn)
        nextBtn.visibility = View.VISIBLE
        nextBtn.setTextColor(ContextCompat.getColor(this, R.color.grey))
        nextBtn.isEnabled = false

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun initializeOutfitCanvas() {
        // Initialize the canvas
        canvasView = findViewById(R.id.canvasView)
        canvasView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun initializeWardrobe() {
        tabLayout = findViewById(R.id.wardrobe_tablayout)
        viewPager2 = findViewById(R.id.wardrobe_viewpager)
        adapter = WardrobeFragmentAdapter(supportFragmentManager, lifecycle, false, this)
        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    override fun updateCanvas(isChecked: Boolean, clothesItem: ClothesItem, bitmap: Bitmap) {
        if (isChecked) {
            outfitClothes.add(clothesItem)
            canvasView.addItem( clothesItem, bitmap)
        } else {
            outfitClothes.remove(clothesItem)
            canvasView.removeItem(clothesItem)
        }

        if (outfitClothes.size > 0) {
            nextBtn.setTextColor(ContextCompat.getColor(this, R.color.pink))
            nextBtn.isEnabled = true
        }
        else {
            nextBtn.setTextColor(ContextCompat.getColor(this, R.color.grey))
            nextBtn.isEnabled = false
        }
    }

}