package com.mobdeve.s12.aiwear

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class NotificationsViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView){
    private val notifLayout: ConstraintLayout
    private val notifIconIv: ImageView
    private val notifHeadlineTv: TextView
    private val notifBodyTv: TextView
    private val notifDateTv: TextView

    init {
        notifLayout = itemView.findViewById(R.id.notifLayout)
        notifIconIv = itemView.findViewById(R.id.notifIconIv)
        notifHeadlineTv = itemView.findViewById(R.id.notifHeadlineTv)
        notifBodyTv = itemView.findViewById(R.id.notifBodyTv)
        notifDateTv = itemView.findViewById(R.id.notifDateTv)
    }
    fun bindData(notifData: NotificationModel) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        this.notifIconIv.setImageResource()
        this.notifHeadlineTv.text = notifData.headline
        this.notifBodyTv.text = notifData.body
        this.notifDateTv.text = dateFormat.format(notifData.date)
        setReadBackground(notifData.read)
    }

    fun setReadBackground(read: Boolean) {
        val colorResId = if (read) {
            R.color.white_sagad
        } else {
            R.color.notif_unread
        }
        val color = ContextCompat.getColor(context, colorResId)
        notifLayout.setBackgroundColor(color)
    }

}