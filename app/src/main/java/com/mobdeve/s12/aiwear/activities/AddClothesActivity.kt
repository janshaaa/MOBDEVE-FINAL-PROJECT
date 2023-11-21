package com.mobdeve.s12.aiwear.activities

import com.mobdeve.s12.aiwear.models.SharedViewModel
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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.fragments.BaseClothesFragment
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
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
    private val sharedViewModel: SharedViewModel by viewModels()
    private var mAuth = FirebaseAuth.getInstance()

    // for image upload
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
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
                uploadImageToFirebaseStorage(imageBitmap) // Save the image path after saving the image

                // set the ImageView to show the bitmap.
                imageButton.setImageBitmap(imageBitmap)
                // consider storing the Bitmap as well if needed for later
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
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(cameraIntent)
        } else {
            // request permission to open camera
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission granted
            openCamera()
        } else {
            Toast.makeText(
                this,
                "Permission to access camera denied.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadImageToFirebaseStorage(bitmap: Bitmap) {
        // Convert the Bitmap to bytes
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        // Generate a random UUID for the image file name
        val imageName = "${UUID.randomUUID()}.jpg"

        // Create a reference to the Firebase Storage path
        val imageRef = storageReference.child("images/$imageName")

        // Upload the image to Firebase Storage
        val uploadTask = imageRef.putBytes(imageData)

        // Register observers to listen for when the upload is successful or if it fails
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image upload successful, get the download URL
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Save the download URL
                imagePath = uri.toString()
            }.addOnFailureListener { exception ->
                // Handle failures of getting download URL
            }
        }.addOnFailureListener { exception ->
            // Handle failures of image upload
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val directory = applicationContext.filesDir
        // create a file to save the image.
        val file = File(directory, "${UUID.randomUUID()}.jpg")

        try {
            // use a FileOutputStream to write data to the file.
            val stream: OutputStream = FileOutputStream(file)
            // compress the bitmap with JPEG format and 100% quality.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            // flush and close the output stream.
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

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

        val newItem = imagePath?.let {
            ClothesItem(mAuth.currentUser!!.uid, name,
                it, category, size, color, material, brand)
        }

        newItem?.let {
            runBlocking { FirestoreDatabaseHandler.addClothesItemToWardrobe(it) }
        }

        sharedViewModel.notifyListUpdate()
        setResult(RESULT_OK)
        finish()
    }

}
