package com.mobdeve.s12.aiwear.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.models.ForumCommentModel
import com.mobdeve.s12.aiwear.models.UserModel
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.runBlocking

class ForumCommentAdapter(private val comments: List<ForumCommentModel>) :
    RecyclerView.Adapter<ForumCommentAdapter.ForumCommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumCommentAdapter.ForumCommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return ForumCommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForumCommentAdapter.ForumCommentViewHolder, position: Int) {
        val comment = comments[position]
        // Bind data to the ViewHolder views here
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    inner class ForumCommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val commentIv = itemView.findViewById<CircleImageView>(R.id.commentCreatorIv)
        val commentUsernameTv = itemView.findViewById<TextView>(R.id.commentUsernameTv)
        val commentCreatedTv = itemView.findViewById<TextView>(R.id.commentCreatedTv)
        val commentContentTv = itemView.findViewById<TextView>(R.id.commentContentTv)

        fun bind(comment: ForumCommentModel) {
            val commenter = runBlocking { FirestoreDatabaseHandler.getUserByUuid(comment.created_by) }!!

            Glide.with(itemView.context).load(commenter.photoUrl).into(commentIv)
            commentUsernameTv.text = commenter.userName
            commentCreatedTv.text = UserModel.DATE_FORMAT.format(comment.created_at)
            commentContentTv.text = comment.content
        }
    }
}