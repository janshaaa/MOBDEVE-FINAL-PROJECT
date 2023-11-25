package com.mobdeve.s12.aiwear.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.activities.CreatePostActivity
import com.mobdeve.s12.aiwear.activities.ForumPostActivity
import com.mobdeve.s12.aiwear.models.ForumPostModel
import com.mobdeve.s12.aiwear.models.UserModel
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Text

class ForumPostAdapter(private val posts: List<ForumPostModel>) :
    RecyclerView.Adapter<ForumPostAdapter.ForumViewHolder>() {

    lateinit var postCreator: UserModel
    lateinit var currentUser: UserModel

    companion object {
        fun passExtras(intent: Intent, post: ForumPostModel, postCreator: UserModel) {
            intent.putExtra(ForumPostModel.POST_ID_KEY, post.post_id)
            intent.putExtra(ForumPostModel.POST_TITLE_KEY, post.title)
            intent.putExtra(ForumPostModel.POST_CONTENT_KEY, post.content)
            intent.putExtra(ForumPostModel.POST_PHOTOURL_KEY, post.photoUrl)
            intent.putExtra(ForumPostModel.POST_CREATED_BY_KEY, post.created_by)
            intent.putExtra(
                ForumPostModel.POST_CREATED_AT_KEY,
                ForumPostModel.DATE_FORMAT.format(post.created_at)
            )
            intent.putExtra(
                ForumPostModel.POST_LAST_MODIFIED_KEY,
                ForumPostModel.DATE_FORMAT.format(post.last_modified_at)
            )
            intent.putExtra(ForumPostModel.POST_LIKES_KEY, post.likes)
            intent.putExtra(ForumPostModel.USER_NAME_KEY, postCreator.userName)
            intent.putExtra(ForumPostModel.USER_PHOTOURL_KEY, postCreator.photoUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum_post_preview, parent, false)
        return ForumViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForumViewHolder, position: Int) {
        val post = posts[position]
        // Bind data to the ViewHolder views here
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    inner class ForumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize and bind views from item_forum_post_preview layout
        private val menuBtn: ToggleButton = itemView.findViewById(R.id.imageButton3)
        private val postCreatorIv: CircleImageView = itemView.findViewById(R.id.postCreatorIv)
        private val usernameTv: TextView = itemView.findViewById(R.id.usernameTextView1)
        private val createdAtTv: TextView = itemView.findViewById(R.id.createdAtTv)
        private val postIv: ImageView = itemView.findViewById(R.id.postIv)
        private val postTitleTv: TextView = itemView.findViewById(R.id.postTitleTv)
        private val postContentTv: TextView = itemView.findViewById(R.id.postContentTv)
        private val postLikeIv: ImageView = itemView.findViewById(R.id.postLikeIv)
        private val postLikesTv: TextView = itemView.findViewById(R.id.postLikesTv)
        private val postCommentsTv: TextView = itemView.findViewById(R.id.postCommentsTv)


        fun bind(post: ForumPostModel) {
            postCreator = runBlocking { FirestoreDatabaseHandler.getUserByUuid(post.created_by) }!!
            currentUser =
                runBlocking { FirestoreDatabaseHandler.getUserByUuid(FirebaseAuth.getInstance().currentUser!!.uid) }!!

            Glide.with(itemView.context).load(postCreator.photoUrl).into(postCreatorIv)
            usernameTv.text = postCreator.userName
            createdAtTv.text = ForumPostModel.DATE_FORMAT.format(post.created_at)
            Glide.with(itemView.context).load(post.photoUrl).into(postIv)
            postTitleTv.text = post.title
            postContentTv.text = post.content
            if (post.post_id in currentUser.likedPosts) {
                postLikeIv.setImageResource(R.drawable.baseline_star_24)
            }
            postLikesTv.text = post.likes.toString()
            postCommentsTv.text = post.getCommentsCount().toString()

            if (currentUser.uuid == postCreator.uuid) {
                menuBtn.visibility = View.VISIBLE
            } else {
                menuBtn.visibility = View.GONE
            }

            val popUp = itemView.findViewById<LinearLayout>(R.id.postLL)
            val editPostBtn = itemView.findViewById<Button>(R.id.editPostBtn)
            val deletePostBtn = itemView.findViewById<Button>(R.id.deletePostBtn)

            menuBtn.textOn = ""
            menuBtn.textOff = ""
            menuBtn.text = null
            menuBtn.setOnClickListener {
                if (menuBtn.isChecked) {
                    popUp.visibility = View.VISIBLE

                    editPostBtn.setOnClickListener {
                        val editPostIntent = Intent(itemView.context, CreatePostActivity::class.java)
                        passExtras(editPostIntent, post, postCreator)
                        itemView.context.startActivity(editPostIntent)
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
            itemView.setOnClickListener {
                val postIntent = Intent(itemView.context, ForumPostActivity::class.java)
                passExtras(postIntent, post, postCreator)
                itemView.context.startActivity(postIntent)
            }

            val likeIv = itemView.findViewById<ImageView>(R.id.postLikeIv)
            currentUser.likedPosts = runBlocking { FirestoreDatabaseHandler.getUserLikes(currentUser.uuid) }
            if (post.post_id in currentUser.likedPosts) {
                likeIv.setImageResource(R.drawable.baseline_star_24)
            }
            else {
                likeIv.setImageResource(R.drawable.outline_star_border_24)
            }
        }

        private fun showDeletePostConfirmationDialog(post: ForumPostModel) {
            val builder = AlertDialog.Builder(itemView.context)

            builder.setTitle("Confirm Post Deletion")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete") { dialog, which ->
                    // Handle account deletion here
                    runBlocking { FirestoreDatabaseHandler.deletePost(post) }
                    Toast.makeText(
                        itemView.context,
                        "Post successfully deleted.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("Cancel") { dialog, which ->
                }
                .show()
        }
    }
}
