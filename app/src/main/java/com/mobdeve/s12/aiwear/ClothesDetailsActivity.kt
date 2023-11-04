package com.mobdeve.s12.aiwear

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide


class ClothesDetailsActivity : AppCompatActivity() {


    private lateinit var clothesImage: ImageView
    private lateinit var clothesName: EditText
    private lateinit var clothesCategory: EditText
    private lateinit var clothesSize: EditText
    private lateinit var clothesColor: EditText
    private lateinit var clothesMaterial: EditText
    private lateinit var clothesBrand: EditText
    private var imagePath: String? = null
    private var imageResId: Int = R.drawable.imageerror

    private lateinit var saveButton: Button
    private lateinit var backButton: ImageButton

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clothes_details)

        clothesImage = findViewById(R.id.clothes_image_details)
        clothesName = findViewById(R.id.name_input)
        clothesCategory = findViewById(R.id.category_input)
        clothesSize = findViewById(R.id.size_input)
        clothesColor = findViewById(R.id.color_input)
        clothesMaterial = findViewById(R.id.material_input)
        clothesBrand = findViewById(R.id.brand_input)
        saveButton = findViewById(R.id.clothessavebutton)

        // Retrieve the details from the intent
        imagePath = intent.getStringExtra("clothesItem_imagePath")
        imageResId = intent.getIntExtra("clothesItem_imageResId", R.drawable.imageerror)
        val itemName = intent.getStringExtra("clothesItem_name") ?: ""
        val itemCategory = intent.getStringExtra("clothesItem_category") ?: ""
        val itemSize = intent.getStringExtra("clothesItem_size") ?: ""
        val itemColor = intent.getStringExtra("clothesItem_color") ?: ""
        val itemMaterial = intent.getStringExtra("clothesItem_material") ?: ""
        val itemBrand = intent.getStringExtra("clothesItem_brand") ?: ""
        val position = intent.getIntExtra("position", -1) // Assume -1 as invalid position

        // Set the retrieved item details to the views
        if (!imagePath.isNullOrEmpty()) {
            // Use a library like Glide or Picasso to load the image from a path.
            Glide.with(this).load(imagePath).into(clothesImage)
        } else {
            clothesImage.setImageResource(imageResId)
        }
        clothesName.setText(itemName)
        clothesCategory.setText(itemCategory)
        clothesSize.setText(itemSize)
        clothesColor.setText(itemColor)
        clothesMaterial.setText(itemMaterial)
        clothesBrand.setText(itemBrand)


        saveButton = findViewById(R.id.clothessavebutton)
        backButton = findViewById(R.id.details_back_button)

        clothesName.addTextChangedListener(textChangeListener)
        clothesCategory.addTextChangedListener(textChangeListener)
        clothesSize.addTextChangedListener(textChangeListener)
        clothesColor.addTextChangedListener(textChangeListener)
        clothesMaterial.addTextChangedListener(textChangeListener)
        clothesBrand.addTextChangedListener(textChangeListener)

        saveButton.setOnClickListener {
            saveChanges(position)
        }

        backButton.setOnClickListener {
            finish()
        }



    }

    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            saveButton.isEnabled = true
        }
    }

    private fun saveChanges(position: Int) {
        if (position == -1) {
            // Handle invalid position if necessary
            return
        }
        val updatedName = clothesName.text.toString()
        val updatedCategory = clothesCategory.text.toString()
        val updatedSize = clothesSize.text.toString()
        val updatedColor = clothesColor.text.toString()
        val updatedMaterial = clothesMaterial.text.toString()
        val updatedBrand = clothesBrand.text.toString()


        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString("clothesItem_name_$position", updatedName)
        editor.putString("clothesItem_category_$position", updatedCategory)
        editor.putString("clothesItem_size_$position", updatedSize)
        editor.putString("clothesItem_color_$position", updatedColor)
        editor.putString("clothesItem_material_$position", updatedMaterial)
        editor.putString("clothesItem_brand_$position", updatedBrand)
        editor.apply()

        val resultIntent = Intent()
        resultIntent.putExtra("updatedName", updatedName)
        resultIntent.putExtra("updatedCategory", updatedCategory)
        resultIntent.putExtra("updatedSize", updatedSize)
        resultIntent.putExtra("updatedColor", updatedColor)
        resultIntent.putExtra("updatedMaterial", updatedMaterial)
        resultIntent.putExtra("updatedBrand", updatedBrand)
        resultIntent.putExtra("position", position)


        setResult(Activity.RESULT_OK, resultIntent)
        finish()

        saveButton.isEnabled = false
    }
}






