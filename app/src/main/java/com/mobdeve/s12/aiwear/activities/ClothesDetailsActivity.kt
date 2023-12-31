package com.mobdeve.s12.aiwear.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import kotlinx.coroutines.runBlocking


class ClothesDetailsActivity : AppCompatActivity() {

    private lateinit var clothesId: String
    private lateinit var originalName: String
    private lateinit var originalCategory: String
    private lateinit var originalSize: String
    private lateinit var originalColor: String
    private lateinit var originalMaterial: String
    private lateinit var originalBrand: String

    private lateinit var clothesImage: ImageView
    private lateinit var clothesName: EditText
    private lateinit var clothesCategorySpinner: Spinner
    private lateinit var clothesSize: EditText
    private lateinit var clothesColor: EditText
    private lateinit var clothesMaterial: EditText
    private lateinit var clothesBrand: EditText
    private var imagePath: String? = null
    private var imageResId: Int = R.drawable.imageerror

    private lateinit var saveButton: Button
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clothes_details)

        clothesImage = findViewById(R.id.clothes_image_details)
        clothesName = findViewById(R.id.name_input)
        clothesCategorySpinner = findViewById(R.id.edit_category_spinner)
        clothesSize = findViewById(R.id.size_input)
        clothesColor = findViewById(R.id.color_input)
        clothesMaterial = findViewById(R.id.material_input)
        clothesBrand = findViewById(R.id.brand_input)
        saveButton = findViewById(R.id.clothessavebutton)

        val categories = resources.getStringArray(R.array.clothes_categories)
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        clothesCategorySpinner.adapter = categoryAdapter



        // retrieve the details from the intent
        clothesId = intent.getStringExtra("clothesItem_id").toString()
        imagePath = intent.getStringExtra("clothesItem_imagePath").toString()
        val itemName = intent.getStringExtra("clothesItem_name") ?: ""
        val itemCategory = intent.getStringExtra("clothesItem_category") ?: ""
        val itemSize = intent.getStringExtra("clothesItem_size") ?: ""
        val itemColor = intent.getStringExtra("clothesItem_color") ?: ""
        val itemMaterial = intent.getStringExtra("clothesItem_material") ?: ""
        val itemBrand = intent.getStringExtra("clothesItem_brand") ?: ""
        val position = intent.getIntExtra("position", -1) // Assume -1 as invalid position

        val currentCategoryIndex = categories.indexOf(itemCategory)
        if (currentCategoryIndex >= 0) {
            clothesCategorySpinner.setSelection(currentCategoryIndex)
        }


        // set the retrieved item details to the views
        if (!imagePath.isNullOrEmpty()) {
            Glide.with(this).load(imagePath).into(clothesImage)
        } else {
            clothesImage.setImageResource(imageResId)
        }
        clothesName.setText(itemName)
        clothesSize.setText(itemSize)
        clothesColor.setText(itemColor)
        clothesMaterial.setText(itemMaterial)
        clothesBrand.setText(itemBrand)

        originalName = itemName
        originalCategory = itemCategory
        originalSize = itemSize
        originalColor = itemColor
        originalMaterial = itemMaterial
        originalBrand = itemBrand

        saveButton = findViewById(R.id.clothessavebutton)
        backButton = findViewById(R.id.details_back_button)

        // check when something is changed
        clothesName.addTextChangedListener(textChangeListener)
        clothesSize.addTextChangedListener(textChangeListener)
        clothesColor.addTextChangedListener(textChangeListener)
        clothesMaterial.addTextChangedListener(textChangeListener)
        clothesBrand.addTextChangedListener(textChangeListener)

        clothesCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                saveButton.isEnabled = hasChanges()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        saveButton.setOnClickListener {
            saveChanges(position)
        }

        backButton.setOnClickListener {
            if(saveButton.isEnabled){
                Toast.makeText(
                    this,
                    "You have unsaved changes.",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                finish()
            }
        }
    }

    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            saveButton.isEnabled = hasChanges()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            saveButton.isEnabled = true
        }
    }

    private fun hasChanges(): Boolean {
        val currentName = clothesName.text.toString()
        val currentCategory = clothesCategorySpinner.selectedItem.toString()
        val currentSize = clothesSize.text.toString()
        val currentColor = clothesColor.text.toString()
        val currentMaterial = clothesMaterial.text.toString()
        val currentBrand = clothesBrand.text.toString()

        return currentName != originalName ||
                currentCategory != originalCategory ||
                currentSize != originalSize ||
                currentColor != originalColor ||
                currentMaterial != originalMaterial ||
                currentBrand != originalBrand
    }

    private fun saveChanges(position: Int) {

        val updatedName = clothesName.text.toString()
        val updatedCategory = clothesCategorySpinner.selectedItem.toString()
        val updatedSize = clothesSize.text.toString()
        val updatedColor = clothesColor.text.toString()
        val updatedMaterial = clothesMaterial.text.toString()
        val updatedBrand = clothesBrand.text.toString()

        runBlocking { FirestoreDatabaseHandler.updateClothesItemInWardrobe(
            ClothesItem(
                clothesId,
                FirebaseAuth.getInstance().currentUser!!.uid,
                updatedName,
                updatedCategory,
                updatedSize,
                updatedColor,
                updatedMaterial,
                updatedBrand
            )
        ) }

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






