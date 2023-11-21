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
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.adapters.WardrobeFragmentAdapter
import com.mobdeve.s12.aiwear.adapters.OutfitCanvasView
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.models.UserModel
import java.text.SimpleDateFormat

interface OnCanvasUpdateListener {
    fun updateCanvas(clothesItem: ClothesItem)
    fun clearCanvas()
}

class CreateOutfitActivity : AppCompatActivity(), OnCanvasUpdateListener {

    companion object {
        const val SELECTED_DATE_KEY = "selectedDate"
        val DATE_FORMAT = SimpleDateFormat("EEE, MMM dd")
    }

    // selected clothes for outfit
    protected var clothesItemList: ArrayList<ClothesItem> = ArrayList()

    // for outfit canvas
    private lateinit var canvasView: OutfitCanvasView
    private lateinit var canvasBitmap: Bitmap
    private lateinit var canvasPaint: Paint
    private lateinit var drawCanvas: Canvas

    private val PICK_IMAGE_REQUEST = 1

    // for wardrobe selection
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: WardrobeFragmentAdapter
    private var outfitClothes = ArrayList<ClothesItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_outfit)

        val selectedDate = intent.getStringExtra(CreateOutfitActivity.SELECTED_DATE_KEY)

        // Set header
        initializeHeader(selectedDate!!)
        initializeOutfitCanvas()
        initializeWardrobe()
    }

    private fun initializeHeader(selectedDate: String) {
        val headerTv = findViewById<TextView>(R.id.settingsHeaderTv)
        val date = UserModel.DATE_FORMAT.parse(selectedDate)
        val formattedDate = CreateOutfitActivity.DATE_FORMAT.format(date)
        headerTv.text = "OOTD for ${formattedDate}"

        val nextBtn = findViewById<Button>(R.id.nextBtn)
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
        canvasView.setBackgroundColor(Color.GRAY)
        val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.imageerror)
        canvasView.addBitmap(Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ))
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

    override fun updateCanvas(clothesItem: ClothesItem) {
        outfitClothes.add(clothesItem)
        val bitmap: Bitmap = clothesItem.imagePath?.let { path ->
            BitmapFactory.decodeFile(path)
        } ?: BitmapFactory.decodeResource(this.resources, R.drawable.imageerror)

        canvasView.addBitmap(bitmap)
    }

    override fun clearCanvas() {
        val blankBitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
        canvasView.addBitmap(blankBitmap)
    }

}