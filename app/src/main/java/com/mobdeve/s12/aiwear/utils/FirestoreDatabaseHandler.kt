package com.mobdeve.s12.aiwear.utils

import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.mobdeve.s12.aiwear.models.BitmapData
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.models.ForumCommentModel
import com.mobdeve.s12.aiwear.models.ForumPostModel
import com.mobdeve.s12.aiwear.models.OutfitModel
import com.mobdeve.s12.aiwear.models.UserModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.Date
import kotlin.coroutines.coroutineContext

class FirestoreDatabaseHandler {
    companion object {

        // collection keys
        val USER_COLLECTION = "users"
        val USER_LIKES_COLLECTION = "likedPosts"
        val POSTS_COLLECTION = "posts"
        val COMMENTS_COLLECTION = "comments"
        val OUTFITS_COLLECTION = "outfits"
        val WARDROBE_COLLECTION = "wardrobe"

        // USER DB QUERIES
        suspend fun getUserByUuid(uuid: String): UserModel? {
            val firestore = FirebaseFirestore.getInstance()
            return try {
                val result = firestore.collection(USER_COLLECTION).document(uuid).get().await()
                result.toObject(UserModel::class.java)
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error getting user by uuid", e)
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
                Log.e("FirestoreDB", "Error getting user", e)
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
                Log.e("FirestoreDB", "Error setting user", e)
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

        suspend fun getUserLikes(user_uuid: String): ArrayList<String> {
            val firestore = FirebaseFirestore.getInstance()
            val likedPosts = ArrayList<String>()

            return try {
                val result = firestore.collection(USER_COLLECTION)
                    .document(user_uuid)
                    .collection(USER_LIKES_COLLECTION)
                    .get()
                    .await()
                for (item in result.documents){
                    likedPosts.add(item.id)
                }
                likedPosts
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error adding to user liked posts", e)
                likedPosts
            }
        }
        suspend fun addToUserLikedPosts(uuid: String, post_id: String) {
            val firestore = FirebaseFirestore.getInstance()
            val emptyData = emptyMap<String, Any>()
            try {
                firestore.collection(USER_COLLECTION)
                    .document(uuid)
                    .collection(USER_LIKES_COLLECTION)
                    .document(post_id)
                    .set(emptyData)
                    .await()
                
                updatePostLikes(post_id, 1)
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error adding to user liked posts", e)
            }
        }

        suspend fun removeFromUserLikedPosts(uuid: String, post_id: String) {
            val firestore = FirebaseFirestore.getInstance()
            try {
                firestore.collection(USER_COLLECTION)
                    .document(uuid)
                    .collection(USER_LIKES_COLLECTION)
                    .document(post_id)
                    .delete()
                    .await()

                updatePostLikes(post_id, -1)
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error removing from user liked posts", e)
            }
        }


        // CLOTHES DB QUERIES
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
                    .await()

                for (item in result) {
                    clothes.add(item.toObject(ClothesItem::class.java))
                }
                clothes
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error querying clothes items in wardrobe", e)
                clothes
            }
        }

        suspend fun getClothesByCategory(userId: String, category: String): List<ClothesItem> {
            val firestore = FirebaseFirestore.getInstance()
            val clothes = ArrayList<ClothesItem>()

            return try {
                val result = getAllClothes(userId)

                for (item in result) {
                    if(item.category == category)
                        clothes.add(item)
                }
                clothes
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error querying clothes by category in wardrobe", e)
                clothes
            }
        }

        // OUTFITS DB QUERIES
        suspend fun addOutfit(outfit: OutfitModel): String {
            val firestore = FirebaseFirestore.getInstance()

            return try {
                val collectionReference = firestore.collection(USER_COLLECTION)
                    .document(outfit.user_uuid)
                    .collection(OUTFITS_COLLECTION)

                val outfitData = mapOf(
                    "outfit_id" to outfit.outfit_id,
                    "user_uuid" to outfit.user_uuid,
                    "date" to outfit.date,
                    "clothes" to outfit.clothes.map { bitmapData ->
                        mapOf(
                            "id" to bitmapData.id,
                            "left" to bitmapData.left,
                            "top" to bitmapData.top
                            // Add other properties as needed
                        )
                    },
                    "outfitPath" to outfit.outfitPath
                )

                val documentReference = collectionReference.add(outfitData).await()
                val documentId = documentReference.id
                collectionReference.document(documentId)
                    .update("outfit_id", documentId)
                    .await()

                documentId
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error creating outfit", e)
                ""
            }
        }

        suspend fun deleteOutfit(outfit: OutfitModel) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                FirebaseStorageHandler.deleteImage(outfit.outfitPath)

                firestore.collection(USER_COLLECTION)
                    .document(outfit.user_uuid)
                    .collection(OUTFITS_COLLECTION)
                    .document(outfit.outfit_id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error deleting outfit", e)
            }
        }

        suspend fun getAllUserOutfits(uuid: String): ArrayList<OutfitModel> {
            val firestore = FirebaseFirestore.getInstance()
            var outfits = ArrayList<OutfitModel>()

            try {
                val result = firestore.collection(USER_COLLECTION)
                    .document(uuid)
                    .collection(OUTFITS_COLLECTION)
                    .get()
                    .await()

                for (item in result) {
                    val outfitId = item.getString("outfit_id") ?: ""
                    val userUuid = item.getString("user_uuid") ?: ""
                    val outfitDate = item.getDate("date") ?: Date()

                    // Convert the "clothes" array to a MutableList<BitmapData>
                    val clothesDataList = (item.get("clothes") as? List<*>)?.map { clothesItem ->
                        if (clothesItem is Map<*, *>) {
                            val id = clothesItem["id"] as? String ?: ""
                            val left = (clothesItem["left"] as? Double)?.toFloat() ?: 0f
                            val top = (clothesItem["top"] as? Double)?.toFloat() ?: 0f

                            BitmapData(id= id,left= left, top= top)
                        } else {
                            null
                        }
                    }?.filterNotNull()?.toMutableList() ?: mutableListOf()

                    val outfitPath = item.getString("outfitPath") ?: ""

                    val outfit = OutfitModel(outfitId, userUuid, outfitDate, clothesDataList, null, outfitPath)

                    outfits.add(outfit)
                }
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error fetching user outfits", e)
            }
            return outfits
        }

        suspend fun getUserOutfits(uuid: String, date: Date): ArrayList<OutfitModel> {
            val firestore = FirebaseFirestore.getInstance()
            var outfits = ArrayList<OutfitModel>()

            try {
                val result = firestore.collection(USER_COLLECTION)
                    .document(uuid)
                    .collection(OUTFITS_COLLECTION)
                    .get()
                    .await()

                for (item in result) {
                    val outfitId = item.getString("outfit_id") ?: ""
                    val userUuid = item.getString("user_uuid") ?: ""
                    val outfitDate = item.getDate("date") ?: Date()

                    if(outfitDate == date) {
                        // Convert the "clothes" array to a MutableList<BitmapData>
                        val clothesDataList = (item.get("clothes") as? List<*>)?.map { clothesItem ->
                            if (clothesItem is Map<*, *>) {
                                val id = clothesItem["id"] as? String ?: ""
                                val left = (clothesItem["left"] as? Double)?.toFloat() ?: 0f
                                val top = (clothesItem["top"] as? Double)?.toFloat() ?: 0f

                                BitmapData(id= id,left= left, top= top)
                            } else {
                                null
                            }
                        }?.filterNotNull()?.toMutableList() ?: mutableListOf()

                        val outfitPath = item.getString("outfitPath") ?: ""

                        val outfit = OutfitModel(outfitId, userUuid, outfitDate, clothesDataList, null, outfitPath)

                        outfits.add(outfit)
                    }

                }
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error fetching user outfits", e)
            }
            return outfits
        }


        // POSTS DB QUERIES
        suspend fun addPost(post: ForumPostModel): String {
            val firestore = FirebaseFirestore.getInstance()
            var post_id = ""

            try {
                FirebaseStorageHandler.uploadImage(
                    post.photoUrl.toUri(),
                    onSuccess = { downloadUrl ->
                        post.photoUrl = downloadUrl
                        val postsRef = firestore.collection(POSTS_COLLECTION)
                        runBlocking {
                            val docRef = postsRef.add(post).await()
                            post_id = docRef.id

                            postsRef.document(post_id)
                                .update("post_id", post_id)
                                .await()
                        }
                    },
                    onFailure = { e ->
                        Log.e("FirestoreDB", "Error uploading image", e)
                    }
                )

            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error adding post", e)
            }
            return post_id
        }

        suspend fun editPost(post: ForumPostModel) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                val existingPost = firestore.collection(POSTS_COLLECTION)
                    .document(post.post_id)
                    .get()
                    .await()
                    .toObject(ForumPostModel::class.java)

                if (existingPost != null && existingPost.photoUrl != post.photoUrl) {
                    FirebaseStorageHandler.deleteImage(existingPost.photoUrl)
                    FirebaseStorageHandler.uploadImage(
                        post.photoUrl.toUri(),
                        onSuccess = { downloadUrl ->
                            post.photoUrl = downloadUrl
                            runBlocking {
                                firestore.collection(POSTS_COLLECTION)
                                    .document(post.post_id)
                                    .set(post)
                                    .await()
                            }
                        },
                        onFailure = { e ->
                            Log.e("FirestoreDB", "Error adding post", e)
                        }
                    )
                }
                else {
                    runBlocking {
                        firestore.collection(POSTS_COLLECTION)
                            .document(post.post_id)
                            .set(post)
                            .await()
                    }
                }
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error editing post", e)
            }
        }

        suspend fun updatePostLikes(post_id: String, value: Int) {
            val firestore = FirebaseFirestore.getInstance()
            val updateMap = hashMapOf<String, Any>("likes" to FieldValue.increment(value.toDouble()))

            try {
                firestore.collection(POSTS_COLLECTION)
                    .document(post_id)
                    .update(updateMap)
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error editing post", e)
            }
        }

        suspend fun deletePost(post: ForumPostModel) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                deleteAllComments(post.post_id)
                FirebaseStorageHandler.deleteImage(post.photoUrl)
                removePostIdFromLikedPosts(post.post_id)

                firestore.collection(POSTS_COLLECTION)
                    .document(post.post_id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error deleting post", e)
            }
        }

        private suspend fun removePostIdFromLikedPosts(post_id: String) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                // Query all users who have liked the post
                val querySnapshot = firestore.collection(USER_COLLECTION)
                    .whereArrayContains(USER_LIKES_COLLECTION, post_id)
                    .get()
                    .await()

                // Iterate over the users and remove the post_id from their likedPosts
                for (document in querySnapshot) {
                    val userId = document.id

                    // Remove the post_id from the likedPosts array
                    firestore.collection(USER_COLLECTION)
                        .document(userId)
                        .collection(USER_LIKES_COLLECTION)
                        .document(post_id)
                        .delete()
                        .await()
                }
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error removing post_id from likedPosts", e)
            }
        }

        suspend fun getAllPosts(): ArrayList<ForumPostModel>? {
            val firestore = FirebaseFirestore.getInstance()
            val posts = ArrayList<ForumPostModel>()

            return try {
                val result = firestore.collection(POSTS_COLLECTION)
                    .get()
                    .await() // Add the 'await' call here

                for (item in result) {
                    val post = item.toObject(ForumPostModel::class.java)
                    val comments = getAllComments(post.post_id)?: ArrayList()
                    post.setComments(comments)

                    posts.add(post)
//                    posts.add()
                }
                posts
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error querying all posts", e)
                posts
            }
        }

        suspend fun getAllPostsByUser(user_uuid: String): ArrayList<ForumPostModel>? {
            val firestore = FirebaseFirestore.getInstance()
            val posts = ArrayList<ForumPostModel>()

            return try {
                val result = firestore.collection(POSTS_COLLECTION)
                            .whereEqualTo("created_by", user_uuid)
                            .get()
                            .await()

                for (item in result) {
                    posts.add(item.toObject(ForumPostModel::class.java))
                }
                posts
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error querying posts by user", e)
                posts
            }
        }


        // COMMENTS DB QUERIES
        suspend fun addComment(comment: ForumCommentModel) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                val commentsRef = firestore.collection(POSTS_COLLECTION)
                    .document(comment.post_id)
                    .collection(COMMENTS_COLLECTION)

                val docRef = commentsRef.add(comment).await()
                val comment_id = docRef.id
                commentsRef.document(comment_id)
                    .update("comment_id", comment_id)
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error adding comment", e)
            }
        }

        private suspend fun deleteAllComments(post_id: String) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                val result = firestore.collection(POSTS_COLLECTION)
                    .document(post_id)
                    .collection(COMMENTS_COLLECTION)
                    .get()
                    .await() // Add the 'await' call here

                for (item in result) {
                    // Use 'await' on the delete operation as well
                    item.reference.delete().await()
                }
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error deleting all comments", e)
            }
        }


        suspend fun getAllComments(post_id: String): ArrayList<ForumCommentModel>? {
            val firestore = FirebaseFirestore.getInstance()
            val comments = ArrayList<ForumCommentModel>()

            return try {
                val result = firestore.collection(POSTS_COLLECTION)
                    .document(post_id)
                    .collection(COMMENTS_COLLECTION)
                    .get()
                    .await() // Add the 'await' call here

                for (item in result) {
                    comments
                    comments.add(item.toObject(ForumCommentModel::class.java))
                }
                comments
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error querying all posts", e)
                comments
            }
        }
    }


}