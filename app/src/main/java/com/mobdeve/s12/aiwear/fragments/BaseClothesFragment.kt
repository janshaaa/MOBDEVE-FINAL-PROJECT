package com.mobdeve.s12.aiwear.fragments

import com.mobdeve.s12.aiwear.models.SharedViewModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.mobdeve.s12.aiwear.activities.ClothesDetailsActivity
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.adapters.ClothesItemAdapter
import com.mobdeve.s12.aiwear.R

abstract class BaseClothesFragment : Fragment() {

    protected lateinit var recyclerView: RecyclerView
    protected val clothesItemList: ArrayList<ClothesItem> = ArrayList()
    protected lateinit var sharedPreferences: SharedPreferences
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
        sharedPreferences =
            requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

        // save provided data to sharedPreferences
        val isInitialDataSaved = sharedPreferences.getBoolean("initial_data_saved", false)
        if (!isInitialDataSaved) {
            val providedData = provideInitialData()
            saveInitialDataToSharedPreferences(providedData)
        }

        loadClothesFromSharedPreferences()
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        allClothesList = getAllClothesFromSharedPreferences()
        adapter = ClothesItemAdapter(clothesItemList, allClothesList, requireContext(), this)
        recyclerView.adapter = adapter

        // when an item is clicked
        setupclickListener()

        sharedViewModel.clothesUpdateEvent.observe(viewLifecycleOwner) {
            // This will be triggered when an item is deleted in another fragment
            Log.d("BFRAGMENT SHAREDVIEW", "CLOTHESITEMLIST ITEMS: ${clothesItemList.size}")
            loadClothesFromSharedPreferences()
            adapter.notifyDataSetChanged()
        }

        sharedViewModel.refreshListEvent.observe(viewLifecycleOwner) {
            loadClothesFromSharedPreferences()
            adapter.notifyDataSetChanged()


    }

        return view
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

                // Save the updated allClothesList back to SharedPreferences
                saveClothesListToSharedPreferences(allClothesList)
                sharedViewModel.notifyClothesChanged()
                adapter.notifyDataSetChanged()
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
                        putExtra("clothesItem_name", selectedItem.name)
                        selectedItem.imagePath?.let {
                            putExtra("clothesItem_imagePath", it)
                        } ?: selectedItem.imageResId?.let {
                            putExtra("clothesItem_imageResId", it)
                        }
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
        })
    }

    private fun saveInitialDataToSharedPreferences(data: List<ClothesItem>) {
        val editor = sharedPreferences.edit()

        editor.putInt("num_clothes_items", data.size)

        for ((index, item) in data.withIndex()) {
            editor.putString("clothesItem_name_$index", item.name)
            item.imageResId?.let { editor.putInt("clothesItem_imageResId_$index", it) }
            item.imagePath?.let { editor.putString("clothesItem_imagePath_$index", it) }
            editor.putString("clothesItem_category_$index", item.category)
            editor.putString("clothesItem_size_$index", item.size)
            editor.putString("clothesItem_color_$index", item.color)
            editor.putString("clothesItem_material_$index", item.material)
            editor.putString("clothesItem_brand_$index", item.brand)
        }

        editor.putBoolean("initial_data_saved", true)

        editor.apply()
    }

    private fun loadClothesFromSharedPreferences() {
        clothesItemList.clear()

        val numberOfItems = sharedPreferences.getInt("num_clothes_items", 0)
        for (i in 0 until numberOfItems) {
            val defaultResId = R.drawable.imageerror
            val imageResId = sharedPreferences.getInt("clothesItem_imageResId_$i", defaultResId)
            val imagePath = sharedPreferences.getString("clothesItem_imagePath_$i", null)
            val finalImageResId = if (imageResId != defaultResId) imageResId else null

            val item = ClothesItem(
                name = sharedPreferences.getString("clothesItem_name_$i", "") ?: "",
                imageResId = finalImageResId,
                imagePath = imagePath,
                category = sharedPreferences.getString("clothesItem_category_$i", "") ?: "",
                size = sharedPreferences.getString("clothesItem_size_$i", "") ?: "",
                color = sharedPreferences.getString("clothesItem_color_$i", "") ?: "",
                material = sharedPreferences.getString("clothesItem_material_$i", "") ?: "",
                brand = sharedPreferences.getString("clothesItem_brand_$i", "") ?: ""
            )
            if (isCategoryItem(item)) {
                clothesItemList.add(item)

            }

        }
        Log.d("BFRAGMENT LOAD", "CLOTHESITEMLIST ITEMS: ${clothesItemList.size}")
    }

    override fun onResume() {
        super.onResume()
        filterData("")
        Log.d("RESUMING FRAGMENT:", "INSIDE")
        Log.d("BFRAGEMENT RESUME", "CLOTHESLIST NO. OF ITEMS: ${clothesItemList.size}")
        loadClothesFromSharedPreferences()
        adapter.notifyDataSetChanged()

    }

    fun saveClothesList(allClothesListUpdated: List<ClothesItem>) {
        Log.d("BFRAGEMENT", "ALLCLOTHESLIST NO. OF ITEMS: ${allClothesListUpdated.size}")
        saveClothesListToSharedPreferences(allClothesListUpdated)

        sharedViewModel.notifyClothesChanged()
        Log.d("SAVECLOTHESLIST", "SAVING NEW LIST")
    }

    private fun saveClothesListToSharedPreferences(allClothesListUpdated: List<ClothesItem>) {
        val editor = sharedPreferences.edit()

        editor.putInt("num_clothes_items", allClothesListUpdated.size)

        // save each item's data
        for ((index, item) in allClothesListUpdated.withIndex()) {
            editor.putString("clothesItem_name_$index", item.name)
            item.imageResId?.let { editor.putInt("clothesItem_imageResId_$index", it) }
            item.imagePath?.let { editor.putString("clothesItem_imagePath_$index", it) }
            editor.putString("clothesItem_category_$index", item.category)
            editor.putString("clothesItem_size_$index", item.size)
            editor.putString("clothesItem_color_$index", item.color)
            editor.putString("clothesItem_material_$index", item.material)
            editor.putString("clothesItem_brand_$index", item.brand)
            Log.d("SAVETOSHAREDPREF", "SAVING ${item.name}\n")
        }

        // remove any leftover data if the current list is smaller than what was saved before
        for (i in allClothesListUpdated.size until sharedPreferences.getInt("num_clothes_items", 0)) {
            editor.remove("clothesItem_name_$i")
            editor.remove("clothesItem_imageResId_$i")
            editor.remove("clothesItem_imagePath_$i")
            editor.remove("clothesItem_category_$i")
            editor.remove("clothesItem_size_$i")
            editor.remove("clothesItem_color_$i")
            editor.remove("clothesItem_material_$i")
            editor.remove("clothesItem_brand_$i")
        }

        editor.apply()


        adapter.notifyDataSetChanged()
        sharedViewModel.notifyClothesChanged()

    }

    protected fun getAllClothesFromSharedPreferences(): ArrayList<ClothesItem> {
        val allClothesList = ArrayList<ClothesItem>()
        val numberOfItems = sharedPreferences.getInt("num_clothes_items", 0)

        for (i in 0 until numberOfItems) {
            val name = sharedPreferences.getString("clothesItem_name_$i", null)
            val imagePath = sharedPreferences.getString("clothesItem_imagePath_$i", null)
            val imageResId = if (imagePath == null) sharedPreferences.getInt("clothesItem_imageResId_$i",
                R.drawable.imageerror
            ) else null
            val category = sharedPreferences.getString("clothesItem_category_$i", null)
            val size = sharedPreferences.getString("clothesItem_size_$i", null)
            val color = sharedPreferences.getString("clothesItem_color_$i", null)
            val material = sharedPreferences.getString("clothesItem_material_$i", null)
            val brand = sharedPreferences.getString("clothesItem_brand_$i", null)

            if (name != null && category != null && size != null && color != null && material != null && brand != null) {
                allClothesList.add(
                    ClothesItem(
                        name = name,
                        imagePath = imagePath,
                        imageResId = imageResId,
                        category = category,
                        size = size,
                        color = color,
                        material = material,
                        brand = brand
                    )
                )
            }
        }

        return allClothesList
    }

    fun filterData(query: String) {
        adapter.filter(query)
    }

    companion object {
        const val PREF_NUM_CLOTHES_ITEMS = "num_clothes_items"
    }
}