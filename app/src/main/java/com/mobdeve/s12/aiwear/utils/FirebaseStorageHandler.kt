package com.mobdeve.s12.aiwear.utils

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class FirebaseStorageHandler {

    companion object {

        const val POST_IMAGES_KEY = "post_images/"
        const val CLOTHES_IMAGES_KEY = "clothes_images/"

        fun uploadImage(imageUri: Uri?, onComplete: (String) -> Unit) {
            val storage: FirebaseStorage = FirebaseStorage.getInstance()
            if (imageUri != null) {
                val storageReference: StorageReference = storage.reference
                val imageRef: StorageReference = storageReference.child(POST_IMAGES_KEY + UUID.randomUUID().toString())

                imageRef.putFile(imageUri)
                    .addOnSuccessListener {
                        // Image uploaded successfully
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Save the download URL
                            onComplete(uri.toString())
                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle failures
                        Log.w("FirebaseStorage", "Error uploading image $e")
                    }
            }
        }

        fun uploadBitmap(bitmap: Bitmap, onComplete: (String) -> Unit) {
            val storage: FirebaseStorage = FirebaseStorage.getInstance()
            val storageReference: StorageReference = storage.reference
            // Convert the Bitmap to bytes
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageData = baos.toByteArray()

            // Generate a random UUID for the image file name
            val imageName = "${UUID.randomUUID()}.jpg"

            // Create a reference to the Firebase Storage path
            val imageRef = storageReference.child(CLOTHES_IMAGES_KEY + imageName)

            // Upload the image to Firebase Storage
            val uploadTask = imageRef.putBytes(imageData)
            // Register observers to listen for when the upload is successful or if it fails
            uploadTask.addOnSuccessListener { taskSnapshot ->
                // Image upload successful, get the download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Save the download URL
                    val imagePath = uri.toString()
                    onComplete(imagePath)
                }.addOnFailureListener { exception ->
                    Log.e("FirebaseStorage", "Error uploading bitmap $exception 2")
                }
            }.addOnFailureListener { exception ->
                Log.e("FirebaseStorage", "Error uploading bitmap $exception 1")
            }
        }
    }

}