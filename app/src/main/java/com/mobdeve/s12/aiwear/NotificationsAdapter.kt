package com.mobdeve.s12.aiwear

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class NotificationsAdapter (data: ArrayList<NotificationModel>) : RecyclerView.Adapter<NotificationsViewHolder>() {
    private val data: ArrayList<NotificationModel>

    init {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_notification, parent, false)
        return NotificationsViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
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


}