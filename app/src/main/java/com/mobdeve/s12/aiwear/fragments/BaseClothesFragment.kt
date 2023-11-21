package com.mobdeve.s12.aiwear.fragments

import com.mobdeve.s12.aiwear.models.SharedViewModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.activities.ClothesDetailsActivity
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.adapters.ClothesItemAdapter
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.activities.OnCanvasUpdateListener
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import kotlinx.coroutines.runBlocking

abstract class BaseClothesFragment(private val isInHomeActivity: Boolean, private val canvasUpdateListener: OnCanvasUpdateListener? = null) : Fragment() {

    protected lateinit var recyclerView: RecyclerView
    protected val clothesItemList: ArrayList<ClothesItem> = ArrayList()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    protected lateinit var adapter: ClothesItemAdapter
    protected var allClothesList: ArrayList<ClothesItem> = ArrayList()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CREATING FRAGMENT:", "INSIDE")

        val view = inflater.inflate(R.layout.fragment_all_clothes, container, false)
        recyclerView = view.findViewById(R.id.allRecyclerView)

        loadWardrobe()
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        allClothesList = runBlocking { FirestoreDatabaseHandler.getAllClothes(currentUser!!.uid) }
        adapter = ClothesItemAdapter(clothesItemList, allClothesList, requireContext(), this, false)
        if(isInHomeActivity)
            adapter = ClothesItemAdapter(clothesItemList, allClothesList, requireContext(), this, true)
        recyclerView.adapter = adapter

        // when an item is clicked
        setupclickListener()

        sharedViewModel.clothesUpdateEvent.observe(viewLifecycleOwner) {
            // This will be triggered when an item is deleted in another fragment
            Log.d("BFRAGMENT SHAREDVIEW", "CLOTHESITEMLIST ITEMS: ${clothesItemList.size}")
            loadWardrobe()
            adapter.notifyDataSetChanged()
        }

        sharedViewModel.refreshListEvent.observe(viewLifecycleOwner) {
            loadWardrobe()
            adapter.notifyDataSetChanged()
        }

        return view
    }

    private fun loadWardrobe() {
        clothesItemList.clear()
        val clothes = runBlocking { FirestoreDatabaseHandler.getAllClothes(currentUser!!.uid) }

        for (c in clothes) {
            clothesItemList.add(c)
        }
    }

    abstract fun isCategoryItem(item: ClothesItem): Boolean

    abstract fun provideInitialData(): List<ClothesItem>

    private fun clothesDetailsActivityResult(data: Intent?) {
        val position = data?.getIntExtra("position", -1) ?: -1
        if (position != -1) {
            val itemToUpdate = allClothesList.find { it.name == clothesItemList[position].name }

            if (itemToUpdate != null) {
                // update the details in the itemToUpdate reference which points to the item in allClothesList
                if (data != null) {
                    itemToUpdate.name = data.getStringExtra("updatedName") ?: itemToUpdate.name
                }
                if (data != null) {
                    itemToUpdate.category = data.getStringExtra("updatedCategory") ?: itemToUpdate.category
                }
                if (data != null) {
                    itemToUpdate.size = data.getStringExtra("updatedSize") ?: itemToUpdate.size
                }
                if (data != null) {
                    itemToUpdate.color = data.getStringExtra("updatedColor") ?: itemToUpdate.color
                }
                if (data != null) {
                    itemToUpdate.material = data.getStringExtra("updatedMaterial") ?: itemToUpdate.material
                }
                if (data != null) {
                    itemToUpdate.brand = data.getStringExtra("updatedBrand") ?: itemToUpdate.brand
                }

                // Update the same details in the clothesItemList, which is specific to this fragment
                clothesItemList[position] = itemToUpdate

                // Save the updated allClothesList
                runBlocking { FirestoreDatabaseHandler.updateClothesItemInWardrobe(itemToUpdate) }
                sharedViewModel.notifyClothesChanged()
                adapter.notifyDataSetChanged()
                sharedViewModel.notifyClothesChanged()
            }
        }
    }

    private fun setupclickListener() {
        val clothesDetailsResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                clothesDetailsActivityResult(result.data)
            }
        }

        adapter.setOnItemClickListener(object : ClothesItemAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (position < clothesItemList.size) {

                    val selectedItem = clothesItemList[position]
                    val intent = Intent(context, ClothesDetailsActivity::class.java).apply {
                        putExtra("clothesItem_id", selectedItem.clothes_id)
                        putExtra("clothesItem_name", selectedItem.name)
                        selectedItem.imagePath?.let {
                            putExtra("clothesItem_imagePath", it)
                        }
//                            ?: selectedItem.imageResId?.let {
//                            putExtra("clothesItem_imageResId", it)
//                        }
                        putExtra("clothesItem_category", selectedItem.category)
                        putExtra("clothesItem_size", selectedItem.size)
                        putExtra("clothesItem_color", selectedItem.color)
                        putExtra("clothesItem_material", selectedItem.material)
                        putExtra("clothesItem_brand", selectedItem.brand)
                        putExtra("position", position)
                    }
                    clothesDetailsResultLauncher.launch(intent)
                }
            }

            override fun onItemCheck(position: Int, isChecked: Boolean, clothesItem: ClothesItem) {
                if (position < clothesItemList.size) {
                    if(isChecked) {
                        canvasUpdateListener!!.updateCanvas(clothesItem)
                    }
                    else {
                        // Remove image from canvas
                        val blankBitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
                        canvasUpdateListener!!.clearCanvas()
                    }
                }
            }
        })
    }


    fun filterData(query: String) {
        adapter.filter(query)
    }

    companion object {
        const val PREF_NUM_CLOTHES_ITEMS = "num_clothes_items"
    }
}