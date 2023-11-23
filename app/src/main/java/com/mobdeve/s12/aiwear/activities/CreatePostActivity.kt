package com.mobdeve.s12.aiwear.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.adapters.ForumPostAdapter
import com.mobdeve.s12.aiwear.models.ForumCommentModel
import com.mobdeve.s12.aiwear.models.ForumPostModel
import com.mobdeve.s12.aiwear.models.UserModel
import com.mobdeve.s12.aiwear.utils.FirebaseStorageHandler
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.util.Date

class CreatePostActivity : AppCompatActivity() {

    private lateinit var postIv: ImageView
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private var post_photo_url = ""
    private lateinit var editedPost: ForumPostModel
    private var postCreator = UserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        var headerTv = findViewById<TextView>(R.id.settingsHeaderTv)
        headerTv.text = "Create Post"


        val post_id = intent.getStringExtra(ForumPostModel.POST_ID_KEY)?: ""
        postCreator.uuid = intent.getStringExtra(ForumPostModel.POST_CREATED_BY_KEY).toString()
        postCreator.userName = intent.getStringExtra(ForumPostModel.USER_NAME_KEY).toString()
        postCreator.photoUrl = intent.getStringExtra(ForumPostModel.USER_PHOTOURL_KEY).toString()

        val postCreatorIv: CircleImageView = findViewById(R.id.postCreatorIv)
        val usernameTv: TextView = findViewById(R.id.usernameTextView1)
        val createdAtTv: TextView = findViewById(R.id.createdAtTv)
        postIv = findViewById(R.id.postIv)
        val postTitleEtv: EditText = findViewById(R.id.postTitleEtv)
        val postContentEtv: EditText = findViewById(R.id.postContentEtv)
        val postBtn: Button = findViewById(R.id.postBtn)

        Glide.with(this).load(postCreator.photoUrl).into(postCreatorIv)
        usernameTv.text = postCreator.userName
        postIv.setOnClickListener {
            openGallery()
        }

        if(post_id == "") {
            postBtn.text = "Post"
        }
        else{
            val post_title = intent.getStringExtra(ForumPostModel.POST_TITLE_KEY).toString()
            val post_content = intent.getStringExtra(ForumPostModel.POST_CONTENT_KEY).toString()
            val post_image = intent.getStringExtra(ForumPostModel.POST_PHOTOURL_KEY).toString()
            val created_at = intent.getStringExtra(ForumPostModel.POST_CREATED_AT_KEY)
            val last_modified = intent.getStringExtra(ForumPostModel.POST_LAST_MODIFIED_KEY)
            val num_likes = intent.getIntExtra(ForumPostModel.POST_LIKES_KEY, 0)
            val num_comments = intent.getIntExtra(ForumPostModel.POST_COMMENTS_COUNT_KEY, 0)
            val comments = intent.getSerializableExtra(ForumPostModel.POST_COMMENTS_KEY) as ArrayList<ForumCommentModel>

            editedPost = ForumPostModel(
                post_id,
                post_title,
                post_content,
                post_image,
                postCreator.uuid,
                ForumPostModel.DATE_FORMAT.parse(created_at),
                ForumPostModel.DATE_FORMAT.parse(last_modified),
                num_likes,
                num_comments,
                comments
            )

            postTitleEtv.setText(post_title)
            postContentEtv.setText(post_content)
            Glide.with(this).load(post_image).into(postIv)
            post_photo_url = post_image
            postIv.imageTintList = null
            postIv.imageTintMode = null
            createdAtTv.text = created_at

            postBtn.text = "Edit"
        }

        postBtn.setOnClickListener {
            if (post_id == "") { // Create Post
                val newPost = ForumPostModel (
                    title = postTitleEtv.text.toString(),
                    content = postContentEtv.text.toString(),
                    photoUrl = post_photo_url,
                    created_by = postCreator.uuid,
                    created_at = Date(),
                    last_modified_at = Date()
                )

                try {
                    runBlocking{ FirestoreDatabaseHandler.addPost(newPost) }
                    Toast.makeText(
                        this,
                        "Successfully posted!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val postIntent = Intent(this, ForumPostActivity::class.java)
                    ForumPostAdapter.passExtras(postIntent, newPost, postCreator)
                    startActivity(postIntent)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Error encountered. Try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                editedPost.title = postTitleEtv.text.toString()
                editedPost.content = postContentEtv.text.toString()
                editedPost.photoUrl = post_photo_url
                editedPost.last_modified_at = Date()

                try {
                    runBlocking{ FirestoreDatabaseHandler.editPost(editedPost) }
                    Toast.makeText(
                        this,
                        "Successfully edited!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val postIntent = Intent(this, ForumPostActivity::class.java)
                    ForumPostAdapter.passExtras(postIntent, editedPost, postCreator)
                    startActivity(postIntent)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Error encountered. Try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            FirebaseStorageHandler.uploadImage(imageUri) { photo_url ->
                post_photo_url = photo_url
                // Callback when upload is complete
                // Load the image into the ImageView using Glide
                postIv.imageTintList = null
                postIv.imageTintMode = null
                Glide.with(this).load(post_photo_url).into(postIv)
            }
        }
    }
}