package com.mobdeve.s12.aiwear

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class AddClothesActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var nameEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var brandEditText: EditText
    private lateinit var sizeEditText: EditText
    private lateinit var materialEditText: EditText
    private lateinit var colorEditText: EditText
    private lateinit var imageButton: ImageButton
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private var imagePath: String? = null


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
        imageButton = findViewById(R.id.add_image)
        val addButton = findViewById<Button>(R.id.clothesaddbutton)
        val backButton: ImageButton = findViewById(R.id.add_back_button)

        addButton.setOnClickListener {
            addClothesItem()
        }


        backButton.setOnClickListener {
            finish()
        }

        imageButton.setOnClickListener {
            openCamera()
        }

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imagePath = saveImageToInternalStorage(imageBitmap) // Save the image path after saving the image

                // Set the ImageView to show the bitmap.
                imageButton.setImageBitmap(imageBitmap)
                // Consider storing the Bitmap as well if needed for later
            }
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

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // You can use the camera
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(cameraIntent)
        } else {
            // Request permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, open camera
            openCamera()
        } else {
            // Permission denied
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        // Get the context's files directory.
        val directory = applicationContext.filesDir
        // Create a file to save the image.
        val file = File(directory, "${UUID.randomUUID()}.jpg")

        try {
            // Use a FileOutputStream to write data to the file.
            val stream: OutputStream = FileOutputStream(file)
            // Compress the bitmap with JPEG format and 100% quality.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            // Flush and close the output stream.
            stream.flush()
            stream.close()
        } catch (e: IOException) { // Handle the exception.
            e.printStackTrace()
        }

        // Return the file path.
        return file.absolutePath
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 100
    }

    private fun addClothesItem() {
//        name, image, category, size, color, material, brand
        val name = nameEditText.text.toString()
        val category = categoryEditText.text.toString()
        val brand = brandEditText.text.toString()
        val size = sizeEditText.text.toString()
        val color = colorEditText.text.toString()
        val material = materialEditText.text.toString()


        val newItem = imagePath?.let { path ->
            ClothesItem(name, path, category, size, color, material, brand)
        } ?: ClothesItem(name, R.drawable.imageerror, category, size, color, material, brand)

        newItem?.let { saveClothesItemToSharedPreferences(it) }

        setResult(RESULT_OK)
        finish()
    }

    private fun saveClothesItemToSharedPreferences(item: ClothesItem) {
        val editor = sharedPreferences.edit()

        val numberOfItems = sharedPreferences.getInt(BaseClothesFragment.PREF_NUM_CLOTHES_ITEMS, 0)
        editor.putInt(BaseClothesFragment.PREF_NUM_CLOTHES_ITEMS, numberOfItems + 1)

        // Save the new item's data
        editor.putString("clothesItem_name_$numberOfItems", item.name)
        item.imagePath?.let { editor.putString("clothesItem_imagePath_$numberOfItems", it) }
        editor.putString("clothesItem_category_$numberOfItems", item.category)
        editor.putString("clothesItem_size_$numberOfItems", item.size)
        editor.putString("clothesItem_color_$numberOfItems", item.color)
        editor.putString("clothesItem_material_$numberOfItems", item.material)
        editor.putString("clothesItem_brand_$numberOfItems", item.brand)

        editor.apply()
    }

}
