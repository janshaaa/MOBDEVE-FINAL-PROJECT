package com.mobdeve.s12.aiwear.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.models.UserModel
import kotlinx.coroutines.tasks.await

class FirestoreDatabaseHandler {
    companion object {

        // collection keys
        val USER_COLLECTION = "users"
        val POSTS_COLLECTION = "forum_posts"
        val COMMENTS_COLLECTION = "comments"
        val OUTFITS_COLLECTION = "outfits"
        val WARDROBE_COLLECTION = "wardrobe"

        suspend fun getUserByUuid(uuid: String): UserModel? {
            val firestore = FirebaseFirestore.getInstance()
            return try {
                val result = firestore.collection(USER_COLLECTION).document(uuid).get().await()
                result.toObject(UserModel::class.java)
            } catch (e: Exception) {
                Log.w("FirestoreDB", e)
                null
            }
        }

        suspend fun getUserByUsername(username: String): UserModel? {
            val firestore = FirebaseFirestore.getInstance()
            val query = firestore.collection(USER_COLLECTION)
                .whereEqualTo("userName", username)
            return getUserFromQuery(query)
        }

        suspend fun isUserNameAvailable(username: String): Boolean {
            val user = getUserByUsername(username)
            return user == null
        }

        private suspend fun getUserFromQuery(query: com.google.firebase.firestore.Query): UserModel? {
            return try {
                val result = query.get().await()
                if (!result.isEmpty) {
                    result.documents[0].toObject(UserModel::class.java)
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.w("FirestoreDB", e)
                null
            }
        }

        suspend fun setUser(user: UserModel) {
            val firestore = FirebaseFirestore.getInstance()
            try {
                firestore.collection(USER_COLLECTION)
                    .document(user.uuid)
                    .set(user)
                    .await()
            } catch (e: Exception) {
                Log.w("FirestoreDB", e)
            }
        }

        suspend fun deleteUser(uuid: String) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                firestore.collection(USER_COLLECTION)
                    .document(uuid)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error deleting clothes item from wardrobe", e)
            }
        }

        suspend fun addClothesItemToWardrobe(clothesItem: ClothesItem) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                val collectionReference = firestore.collection(USER_COLLECTION)
                    .document(clothesItem.user_uuid)
                    .collection(WARDROBE_COLLECTION)

                // Add the document and get the auto-generated document reference
                val documentReference = collectionReference.add(clothesItem).await()

                // Get the document ID from the reference
                val documentId = documentReference.id

                // Update the document with the document ID as an attribute
                collectionReference.document(documentId)
                    .update("clothes_id", documentId)
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error adding clothes item to wardrobe", e)
            }
        }

        suspend fun updateClothesItemInWardrobe(updatedClothesItem: ClothesItem) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                firestore.collection(USER_COLLECTION)
                    .document(updatedClothesItem.user_uuid)
                    .collection(WARDROBE_COLLECTION)
                    .document(updatedClothesItem.clothes_id)
                    .set(updatedClothesItem)
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error updating clothes item in wardrobe", e)
            }
        }

        suspend fun deleteClothesItemFromWardrobe(clothesItem: ClothesItem) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                firestore.collection(USER_COLLECTION)
                    .document(clothesItem.user_uuid)
                    .collection(WARDROBE_COLLECTION)
                    .document(clothesItem.clothes_id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error deleting clothes item from wardrobe", e)
            }
        }

        suspend fun getAllClothes(userId: String): ArrayList<ClothesItem> {
            val firestore = FirebaseFirestore.getInstance()
            val clothes = ArrayList<ClothesItem>()

            return try {
                val result = firestore.collection(USER_COLLECTION)
                    .document(userId)
                    .collection(WARDROBE_COLLECTION)
                    .get()
                    .await() // Add the 'await' call here

                for (item in result) {
                    clothes.add(item.toObject(ClothesItem::class.java))
                }
                clothes
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error querying clothes items in wardrobe", e)
                clothes
            }
        }

        suspend fun getClothesByCategory(userId: String, category: String?): List<ClothesItem> {
            val firestore = FirebaseFirestore.getInstance()
            val clothes = mutableListOf<ClothesItem>()

            return try {
                val query = firestore.collection(USER_COLLECTION)
                    .document(userId)
                    .collection(WARDROBE_COLLECTION)

                val result = if (category.isNullOrEmpty()) {
                    query.get().await()
                } else {
                    query.whereEqualTo("category", category)
                        .get()
                        .await()
                }

                for (item in result) {
                    clothes.add(item.toObject(ClothesItem::class.java))
                }
                clothes
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error querying clothes items in wardrobe", e)
                emptyList()
            }
        }
    }


}