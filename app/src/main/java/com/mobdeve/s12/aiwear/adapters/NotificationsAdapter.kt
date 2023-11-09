package com.mobdeve.s12.aiwear.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.models.NotificationModel
import java.text.SimpleDateFormat

class NotificationsAdapter (data: ArrayList<NotificationModel>) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    private val data: ArrayList<NotificationModel>

    init {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Notification clicked",
                Toast.LENGTH_SHORT
            ).show()
            if(!data[position].read) {
                data[position].read = true
                holder.setReadBackground(data[position].read)
            }
        }
        holder.bindData(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView){
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
}