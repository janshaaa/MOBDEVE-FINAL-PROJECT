package com.mobdeve.s12.aiwear.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID
import kotlin.coroutines.suspendCoroutine

class FirebaseStorageHandler {

    companion object {

        const val POST_IMAGES_KEY = "post_images/"
        const val CLOTHES_IMAGES_KEY = "clothes_images/"
        const val OUTFITS_IMAGES_KEY = "outfits_images/"

        fun uploadImage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
            // Create a unique filename for the image
            val storageReference = FirebaseStorage.getInstance().reference
            val fileName = "${UUID.randomUUID()}.jpg"
            val imageRef: StorageReference = storageReference.child(
                POST_IMAGES_KEY + fileName)

            // Upload file to Firebase Storage
            imageRef.putFile(imageUri)
                .addOnSuccessListener(OnSuccessListener {
                    // Image uploaded successfully
                    imageRef.downloadUrl
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Get the download URL
                                val downloadUrl = task.result.toString()
                                onSuccess.invoke(downloadUrl)
                            } else {
                                // Handle the error
                                onFailure.invoke(task.exception!!)
                            }
                        })
                })
                .addOnFailureListener(OnFailureListener {
                    // Handle unsuccessful upload
                    onFailure.invoke(it)
                })
        }

        fun getBitmapFromPath(imagePath: String, onSuccess: (Bitmap) -> Unit, onError: (Any?) -> Unit) {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imagePath ?: "")

            val maxAllowedSizeBytes: Long = 1024 * 1024 * 5 // 5 MB (adjust as needed)

            storageReference.getBytes(maxAllowedSizeBytes)
                .addOnSuccessListener { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    onSuccess.invoke(bitmap)
                }
                .addOnFailureListener {
                    // Handle the error
                    throw Exception()
                }
        }

        fun uploadClothesBitmap(bitmap: Bitmap, onComplete: (String) -> Unit) {
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

        suspend fun deleteImage(photoUrl: String) {
            val storage = FirebaseStorage.getInstance()

            try {
                val storageReference = storage.getReferenceFromUrl(photoUrl)
                storageReference.delete().await()
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error deleting image in storage", e)
            }
        }

        fun uploadOutfitBitmap(bitmap: Bitmap, onComplete: (String) -> Unit) {
            val storage: FirebaseStorage = FirebaseStorage.getInstance()
            val storageReference: StorageReference = storage.reference
            // Convert the Bitmap to bytes
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageData = baos.toByteArray()

            // Generate a random UUID for the image file name
            val imageName = "${UUID.randomUUID()}.jpg"

            // Create a reference to the Firebase Storage path
            val imageRef = storageReference.child(OUTFITS_IMAGES_KEY + imageName)

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
                    Log.e("FirebaseStorage", "Error uploading outfit bitmap $exception 2")
                }
            }.addOnFailureListener { exception ->
                Log.e("FirebaseStorage", "Error uploading outfit bitmap $exception 1")
            }
        }
    }



}
