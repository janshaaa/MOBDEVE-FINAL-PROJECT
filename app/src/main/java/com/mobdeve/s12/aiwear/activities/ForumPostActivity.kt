package com.mobdeve.s12.aiwear.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.models.ForumCommentModel
import com.mobdeve.s12.aiwear.models.ForumPostModel
import de.hdodenhof.circleimageview.CircleImageView

class ForumPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_post)

        var headerTv = findViewById<TextView>(R.id.settingsHeaderTv)
        headerTv.text = ""

        val postCreatorIv = findViewById<CircleImageView>(R.id.postCreatorIv)
        val usernameTv = findViewById<TextView>(R.id.usernameTextView1)
        val createdAtTv = findViewById<TextView>(R.id.createdAtTv)
        val postIv = findViewById<ImageView>(R.id.postIv)
        val postTitleTv = findViewById<TextView>(R.id.postTitleTv)
        val postContentTv = findViewById<TextView>(R.id.postContentTv)

        val post_id = intent.getStringExtra(ForumPostModel.POST_ID_KEY)
        val post_title = intent.getStringExtra(ForumPostModel.POST_TITLE_KEY)
        val post_content = intent.getStringExtra(ForumPostModel.POST_CONTENT_KEY)
        val post_image = intent.getStringExtra(ForumPostModel.POST_PHOTOURL_KEY)
        val created_by_uuid = intent.getStringExtra(ForumPostModel.POST_CREATED_BY_KEY)
        val created_by_username = intent.getStringExtra(ForumPostModel.USER_NAME_KEY)
        val created_by_photo = intent.getStringExtra(ForumPostModel.USER_PHOTOURL_KEY)
        val created_at = intent.getStringExtra(ForumPostModel.POST_CREATED_AT_KEY)
        val last_modified = intent.getStringExtra(ForumPostModel.POST_LAST_MODIFIED_KEY)
        val num_likes = intent.getIntExtra(ForumPostModel.POST_LIKES_KEY, 0)
        val num_comments = intent.getIntExtra(ForumPostModel.POST_COMMENTS_COUNT_KEY, 0)
        val comments = intent.getSerializableExtra(ForumPostModel.POST_COMMENTS_KEY) as ArrayList<ForumCommentModel>

        Glide.with(this).load(created_by_photo).into(postCreatorIv)
        usernameTv.text = created_by_username
        postTitleTv.text = post_title
        postContentTv.text = post_content
        createdAtTv.text = created_at
        Glide.with(this).load(post_image).into(postIv)

        // Initialize recycler view for comments

    }
}