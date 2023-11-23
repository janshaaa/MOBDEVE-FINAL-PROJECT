package com.mobdeve.s12.aiwear.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.adapters.ForumCommentAdapter
import com.mobdeve.s12.aiwear.adapters.ForumPostAdapter
import com.mobdeve.s12.aiwear.models.ForumCommentModel
import com.mobdeve.s12.aiwear.models.ForumPostModel
import com.mobdeve.s12.aiwear.models.UserModel
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Text

class ForumPostActivity : AppCompatActivity() {

    private var currentUser =
        runBlocking { FirestoreDatabaseHandler.getUserByUuid(FirebaseAuth.getInstance().currentUser!!.uid) }!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var commentsAdapter: ForumCommentAdapter
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

        val post_id = intent.getStringExtra(ForumPostModel.POST_ID_KEY).toString()
        val post_title = intent.getStringExtra(ForumPostModel.POST_TITLE_KEY).toString()
        val post_content = intent.getStringExtra(ForumPostModel.POST_CONTENT_KEY).toString()
        val post_image = intent.getStringExtra(ForumPostModel.POST_PHOTOURL_KEY).toString()
        val created_by_uuid = intent.getStringExtra(ForumPostModel.POST_CREATED_BY_KEY).toString()
        val created_by_username = intent.getStringExtra(ForumPostModel.USER_NAME_KEY)
        val created_by_photo = intent.getStringExtra(ForumPostModel.USER_PHOTOURL_KEY)
        val created_at = intent.getStringExtra(ForumPostModel.POST_CREATED_AT_KEY)
        val last_modified = intent.getStringExtra(ForumPostModel.POST_LAST_MODIFIED_KEY)
        val num_likes = intent.getIntExtra(ForumPostModel.POST_LIKES_KEY, 0)
        val comments = runBlocking { FirestoreDatabaseHandler.getAllComments(post_id) }?: ArrayList<ForumCommentModel>()

        val post = ForumPostModel(
            post_id,
            post_title,
            post_content,
            post_image,
            created_by_uuid,
            ForumPostModel.DATE_FORMAT.parse(created_at),
            ForumPostModel.DATE_FORMAT.parse(last_modified),
            num_likes
        )
        post.setComments(comments)

        Glide.with(this).load(created_by_photo).into(postCreatorIv)
        usernameTv.text = created_by_username
        postTitleTv.text = post_title
        postContentTv.text = post_content
        createdAtTv.text = created_at
        Glide.with(this).load(post_image).into(postIv)

        // Initialize recycler view for comments
        recyclerView = findViewById(R.id.commentsRecyclerView)
        commentsAdapter = ForumCommentAdapter(comments!!)

        // Set the adapter to the RecyclerView
        recyclerView.adapter = commentsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Menu button is post of current user
        val menuBtn = findViewById<ToggleButton>(R.id.imageButton)
        if (currentUser.uuid == created_by_uuid) {
            menuBtn.visibility = View.VISIBLE
        } else {
            menuBtn.visibility = View.GONE
        }

        val popUp = findViewById<LinearLayout>(R.id.postLL)
        val editPostBtn = findViewById<Button>(R.id.editPostBtn)
        val deletePostBtn = findViewById<Button>(R.id.deletePostBtn)

        menuBtn.textOn = ""
        menuBtn.textOff = ""
        menuBtn.text = null
        menuBtn.setOnClickListener {
            if (menuBtn.isChecked) {
                popUp.visibility = View.VISIBLE

                editPostBtn.setOnClickListener {
                    val editPostIntent = Intent(this, CreatePostActivity::class.java)
                    ForumPostAdapter.passExtras(editPostIntent, post, currentUser)
                    startActivity(editPostIntent)
                    menuBtn.performClick()
                }

                deletePostBtn.setOnClickListener {
                    showDeletePostConfirmationDialog(post)
                    menuBtn.performClick()
                }
            } else {
                popUp.visibility = View.GONE
            }
        }

        // Like Button


        // Comment Button
        val commentBtn = findViewById<Button>(R.id.commentBtn)
        commentBtn.setOnClickListener {
//            Toast.makeText(
//                this,
//                "Comment!",
//                Toast.LENGTH_SHORT
//            ).show()
            val newCommentInput = findViewById<LinearLayout>(R.id.newCommentLL)
            newCommentInput.visibility = View.VISIBLE
            val commenterCreatorIv = findViewById<CircleImageView>(R.id.commenterCreatorIv)
            Glide.with(this).load(currentUser.photoUrl).into(commenterCreatorIv)
            val commenterUsernameTv = findViewById<TextView>(R.id.commenterUsernameTv)
            commenterUsernameTv.text = currentUser.userName
            val commentContentEtv = findViewById<EditText>(R.id.commentContentEtv)
            commentContentEtv.visibility = View.VISIBLE
            val postCommentBtn = findViewById<Button>(R.id.postCommentBtn)
            postCommentBtn.visibility = View.VISIBLE
            postCommentBtn.setOnClickListener {
                val newComment = ForumCommentModel(
                    post.post_id,
                    commentContentEtv.text.toString(),
                    currentUser.uuid
                )
                runBlocking { FirestoreDatabaseHandler.addComment(newComment) }
                comments.add(0, newComment)
                commentsAdapter.notifyItemInserted(0)
                commentContentEtv.setText("")
                newCommentInput.visibility = View.GONE
            }
        }
    }

    private fun showDeletePostConfirmationDialog(post: ForumPostModel) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Confirm Account Deletion")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Delete") { dialog, which ->
                // Handle account deletion here
                runBlocking { FirestoreDatabaseHandler.deletePost(post) }
                Toast.makeText(
                    this,
                    "Post successfully deleted.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                Toast.makeText(
                    this,
                    "Error deleting post.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }
}