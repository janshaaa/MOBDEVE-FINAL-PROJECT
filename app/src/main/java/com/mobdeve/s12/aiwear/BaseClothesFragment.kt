package com.mobdeve.s12.aiwear

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager

abstract class BaseClothesFragment : Fragment() {

    protected lateinit var recyclerView: RecyclerView
    protected val clothesItemList: ArrayList<ClothesItem> = ArrayList()
    protected lateinit var sharedPreferences: SharedPreferences
    protected lateinit var adapter: ClothesItemAdapter
    protected var allClothesList: ArrayList<ClothesItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_clothes, container, false)

        recyclerView = view.findViewById(R.id.allRecyclerView)
        sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)


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

        fun filterData(query: String) {
            adapter.filter(query)
        }

        // when an item is clicked
        setupclickListener()

        return view
    }

    abstract fun isCategoryItem(item: ClothesItem): Boolean

    abstract fun provideInitialData(): List<ClothesItem>


    private fun clothesDetailsActivityResult(data: Intent?) {
        val position = data?.getIntExtra("position", -1) ?: -1
        if (position != -1) {
            clothesItemList[position].name = data?.getStringExtra("updatedName") ?: clothesItemList[position].name
            clothesItemList[position].category = data?.getStringExtra("updatedCategory") ?: clothesItemList[position].category
            clothesItemList[position].size = data?.getStringExtra("updatedSize") ?: clothesItemList[position].size
            clothesItemList[position].color = data?.getStringExtra("updatedColor") ?: clothesItemList[position].color
            clothesItemList[position].material = data?.getStringExtra("updatedMaterial") ?: clothesItemList[position].material
            clothesItemList[position].brand = data?.getStringExtra("updatedBrand") ?: clothesItemList[position].brand
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupclickListener() {
        val clothesDetailsResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the result from the ClothesDetailsActivity
                clothesDetailsActivityResult(result.data)
            }
        }

        adapter.setOnItemClickListener(object : ClothesItemAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedItem = clothesItemList[position]
                val intent = Intent(context, ClothesDetailsActivity::class.java).apply {
                    putExtra("clothesItem_name", selectedItem.name)
                    putExtra("clothesItem_image", selectedItem.image)
                    putExtra("clothesItem_category", selectedItem.category)
                    putExtra("clothesItem_size", selectedItem.size)
                    putExtra("clothesItem_color", selectedItem.color)
                    putExtra("clothesItem_material", selectedItem.material)
                    putExtra("clothesItem_brand", selectedItem.brand)
                    putExtra("position", position)
                }
                clothesDetailsResultLauncher.launch(intent)
            }
        })
    }

    private fun saveInitialDataToSharedPreferences(data: List<ClothesItem>) {
        val editor = sharedPreferences.edit()

        editor.putInt("num_clothes_items", data.size) // Use data.size instead of clothesItemList.size

        for ((index, item) in data.withIndex()) {
            editor.putString("clothesItem_name_$index", item.name)
            item.image?.let { editor.putInt("clothesItem_image_$index", it) }
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
            val item = ClothesItem(
                name = sharedPreferences.getString("clothesItem_name_$i", "") ?: "",
                image = sharedPreferences.getInt("clothesItem_image_$i", R.drawable.imageerror),
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
    }

    override fun onResume() {
        super.onResume()
        loadClothesFromSharedPreferences()
        adapter.notifyDataSetChanged()
    }

    fun saveClothesList(allClothesList: List<ClothesItem>) {
        saveClothesListToSharedPreferences(allClothesList)
    }

    private fun saveClothesListToSharedPreferences(allClothesList: List<ClothesItem>) {
        val editor = sharedPreferences.edit()

        editor.putInt("num_clothes_items", allClothesList.size)

        // save each item's data
        for ((index, item) in allClothesList.withIndex()) {
            editor.putString("clothesItem_name_$index", item.name)
            item.image?.let { editor.putInt("clothesItem_image_$index", it) }
            editor.putString("clothesItem_category_$index", item.category)
            editor.putString("clothesItem_size_$index", item.size)
            editor.putString("clothesItem_color_$index", item.color)
            editor.putString("clothesItem_material_$index", item.material)
            editor.putString("clothesItem_brand_$index", item.brand)
        }

        // remove any leftover data if the current list is smaller than what was saved before
        for (i in allClothesList.size until sharedPreferences.getInt("num_clothes_items", 0)) {
            editor.remove("clothesItem_name_$i")
            editor.remove("clothesItem_image_$i")
            editor.remove("clothesItem_category_$i")
            editor.remove("clothesItem_size_$i")
            editor.remove("clothesItem_color_$i")
            editor.remove("clothesItem_material_$i")
            editor.remove("clothesItem_brand_$i")
        }

        editor.apply()
    }

    protected fun getAllClothesFromSharedPreferences(): ArrayList<ClothesItem> {
        val allClothesList = ArrayList<ClothesItem>()
        val numberOfItems = sharedPreferences.getInt("num_clothes_items", 0)

        for (i in 0 until numberOfItems) {
            val name = sharedPreferences.getString("clothesItem_name_$i", null)
            val image = sharedPreferences.getInt("clothesItem_image_$i", R.drawable.imageerror)
            val category = sharedPreferences.getString("clothesItem_category_$i", null)
            val size = sharedPreferences.getString("clothesItem_size_$i", null)
            val color = sharedPreferences.getString("clothesItem_color_$i", null)
            val material = sharedPreferences.getString("clothesItem_material_$i", null)
            val brand = sharedPreferences.getString("clothesItem_brand_$i", null)

            if (name != null && category != null && size != null && color != null && material != null && brand != null) {
                allClothesList.add(
                    ClothesItem(
                        name = name,
                        image = image,
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

    abstract fun filterData(searchQuery: String)

    companion object {
        const val PREF_NUM_CLOTHES_ITEMS = "num_clothes_items"
    }
}
