package com.mobdeve.s12.aiwear.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.models.ForumCommentModel
import com.mobdeve.s12.aiwear.models.ForumPostModel
import com.mobdeve.s12.aiwear.models.UserModel
import kotlinx.coroutines.tasks.await

class FirestoreDatabaseHandler {
    companion object {

        // collection keys
        val USER_COLLECTION = "users"
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


        // POSTS DB QUERIES
        suspend fun addPost(post: ForumPostModel) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                val postsRef = firestore.collection(POSTS_COLLECTION)

                // Add the document and get the auto-generated document reference
                val docRef = postsRef.add(post).await()

                // Get the document ID from the reference
                val post_id = docRef.id

                // Update the document with the document ID as an attribute
                postsRef.document(post_id)
                    .update("post_id", post_id)
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error adding post", e)
            }
        }

        suspend fun editPost(post: ForumPostModel) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                firestore.collection(POSTS_COLLECTION)
                    .document(post.post_id)
                    .set(post)
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error editing post", e)
            }
        }

        suspend fun deletePost(post: ForumPostModel) {
            val firestore = FirebaseFirestore.getInstance()

            try {
                firestore.collection(POSTS_COLLECTION)
                    .document(post.post_id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreDB", "Error deleting post", e)
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