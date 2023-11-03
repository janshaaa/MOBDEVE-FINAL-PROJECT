package com.mobdeve.s12.aiwear

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class AddClothesActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var nameEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var brandEditText: EditText
    private lateinit var sizeEditText: EditText
    private lateinit var materialEditText: EditText
    private lateinit var colorEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_clothes)

        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        nameEditText = findViewById(R.id.add_name)
        categoryEditText = findViewById(R.id.add_category)
        brandEditText = findViewById(R.id.add_brand)
        sizeEditText = findViewById(R.id.add_size)
        materialEditText = findViewById(R.id.add_material)
        colorEditText = findViewById(R.id.add_color)
        val addButton = findViewById<Button>(R.id.clothesaddbutton)
        val backButton: ImageButton = findViewById(R.id.add_back_button)

        addButton.setOnClickListener {
            addClothesItem()
        }


        backButton.setOnClickListener {
            finish()
        }

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                addButton.isEnabled = s?.toString()?.trim()?.isNotEmpty() == true
            }


        })

        addButton.isEnabled = nameEditText.text.toString().trim().isNotEmpty()
    }

    private fun addClothesItem() {
//        name, image, category, size, color, material, brand
        val name = nameEditText.text.toString()
        val category = categoryEditText.text.toString()
        val brand = brandEditText.text.toString()
        val size = sizeEditText.text.toString()
        val color = colorEditText.text.toString()
        val material = materialEditText.text.toString()
        // ... get text from other EditTexts

        // Assuming image is an Int drawable resource identifier, defaulting to a placeholder image
        val image = R.drawable.imageerror

        // Create the ClothesItem object
        val newItem = ClothesItem(name, image, category, size, color, material, brand)

        // Save to SharedPreferences
        saveClothesItemToSharedPreferences(newItem)

        setResult(RESULT_OK)
        finish()
    }

    private fun saveClothesItemToSharedPreferences(item: ClothesItem) {
        val editor = sharedPreferences.edit()

        val numberOfItems = sharedPreferences.getInt(BaseClothesFragment.PREF_NUM_CLOTHES_ITEMS, 0)
        editor.putInt(BaseClothesFragment.PREF_NUM_CLOTHES_ITEMS, numberOfItems + 1)

        // Save the new item's data
        editor.putString("clothesItem_name_$numberOfItems", item.name)
        item.image?.let { editor.putInt("clothesItem_image_$numberOfItems", it) }
        editor.putString("clothesItem_category_$numberOfItems", item.category)
        editor.putString("clothesItem_size_$numberOfItems", item.size)
        editor.putString("clothesItem_color_$numberOfItems", item.color)
        editor.putString("clothesItem_material_$numberOfItems", item.material)
        editor.putString("clothesItem_brand_$numberOfItems", item.brand)

        editor.apply()
    }

}
