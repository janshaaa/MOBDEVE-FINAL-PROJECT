package com.mobdeve.s12.aiwear.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.R
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
        val menuBtn: ImageButton = itemView.findViewById(R.id.imageButton3)
        val postCreatorIv: CircleImageView = itemView.findViewById(R.id.postCreatorIv)
        val usernameTv: TextView = itemView.findViewById(R.id.usernameTextView1)
        val createdAtTv: TextView = itemView.findViewById(R.id.createdAtTv)
        val postIv: ImageView = itemView.findViewById(R.id.postIv)
        val postTitleTv: TextView = itemView.findViewById(R.id.postTitleTv)
        val postContentTv: TextView = itemView.findViewById(R.id.postContentTv)
        val postLikeIv: ImageView = itemView.findViewById(R.id.postLikeIv)
        val postLikesTv: TextView = itemView.findViewById(R.id.postLikesTv)
        val postCommentsTv: TextView = itemView.findViewById(R.id.postCommentsTv)

        fun bind(post: ForumPostModel) {
            postCreator = runBlocking{ FirestoreDatabaseHandler.getUserByUuid(post.created_by) }!!
            currentUser = runBlocking { FirestoreDatabaseHandler.getUserByUuid(FirebaseAuth.getInstance().currentUser!!.uid) }!!

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
            postCommentsTv.text = post.commentsCount.toString()

            if (currentUser.uuid == postCreator.uuid) {
                menuBtn.visibility = View.VISIBLE
            }
            else {
                menuBtn.visibility = View.GONE
            }

            menuBtn.setOnClickListener {
                // popup drawable
            }

            itemView.setOnClickListener {
                val postIntent = Intent(itemView.context, ForumPostActivity::class.java)
                postIntent.putExtra(ForumPostModel.POST_ID_KEY, post.post_id)
                postIntent.putExtra(ForumPostModel.POST_TITLE_KEY, post.title)
                postIntent.putExtra(ForumPostModel.POST_CONTENT_KEY, post.content)
                postIntent.putExtra(ForumPostModel.POST_PHOTOURL_KEY, post.photoUrl)
                postIntent.putExtra(ForumPostModel.POST_CREATED_BY_KEY, post.created_by)
                postIntent.putExtra(ForumPostModel.POST_CREATED_AT_KEY, ForumPostModel.DATE_FORMAT.format(post.created_at))
                postIntent.putExtra(ForumPostModel.POST_LAST_MODIFIED_KEY, ForumPostModel.DATE_FORMAT.format(post.last_modified_at))
                postIntent.putExtra(ForumPostModel.POST_LIKES_KEY, post.likes)
                postIntent.putExtra(ForumPostModel.POST_COMMENTS_COUNT_KEY, post.commentsCount)
                postIntent.putExtra(ForumPostModel.POST_COMMENTS_KEY, post.comments)
                postIntent.putExtra(ForumPostModel.USER_NAME_KEY, postCreator.userName)
                postIntent.putExtra(ForumPostModel.USER_PHOTOURL_KEY, postCreator.photoUrl)
                itemView.context.startActivity(postIntent)
            }
        }


    }
}

