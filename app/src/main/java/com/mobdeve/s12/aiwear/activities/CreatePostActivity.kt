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
import com.mobdeve.s12.aiwear.models.ForumPostModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        var headerTv = findViewById<TextView>(R.id.settingsHeaderTv)
        headerTv.text = "Create Post"

//        val intent = Intent()
        val created_by = intent.getStringExtra(ForumPostModel.POST_CREATED_BY_KEY).toString()
        val username = intent.getStringExtra(ForumPostModel.USER_NAME_KEY).toString()
        val photoUrl = intent.getStringExtra(ForumPostModel.USER_PHOTOURL_KEY)?.toUri()

        val postCreatorIv: CircleImageView = findViewById(R.id.postCreatorIv)
        val usernameTv: TextView = findViewById(R.id.usernameTextView1)
        val createdAtTv: TextView = findViewById(R.id.createdAtTv)
        postIv = findViewById(R.id.postIv)
        val postTitleEtv: EditText = findViewById(R.id.postTitleEtv)
        val postContentEtv: EditText = findViewById(R.id.postContentEtv)

        Glide.with(this).load(photoUrl).into(postCreatorIv)
        usernameTv.text = username
        postIv.setOnClickListener {
            openGallery()
        }

        val postBtn: Button = findViewById(R.id.postBtn)
        postBtn.setOnClickListener {
            val newPost = ForumPostModel (
                title = postTitleEtv.text.toString(),
                content = postContentEtv.text.toString(),
                photoUrl = post_photo_url,
                created_by = created_by,
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
                val forumIntent = Intent(this, ForumActivity::class.java)
                startActivity(forumIntent)
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