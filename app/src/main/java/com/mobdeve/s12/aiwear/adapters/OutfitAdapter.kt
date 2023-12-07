package com.mobdeve.s12.aiwear.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.activities.CreateOutfitActivity
import com.mobdeve.s12.aiwear.activities.ViewOutfitActivity
import com.mobdeve.s12.aiwear.models.OutfitModel
import com.mobdeve.s12.aiwear.models.UserModel
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import kotlinx.coroutines.runBlocking

class OutfitAdapter(private var outfits: ArrayList<OutfitModel>, private val listener: OnOutfitDeleteListener) :
    RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_outfit_preview, parent, false)
        return OutfitViewHolder(view)
    }

    override fun onBindViewHolder(holder: OutfitViewHolder, position: Int) {
        val outfit = outfits[position]
        // Bind data to the ViewHolder views here
        holder.bind(outfit)
    }

    fun updateOutfits(newOutfits: ArrayList<OutfitModel>) {
        outfits = newOutfits
    }

    override fun getItemCount(): Int {
        return outfits.size
    }

    inner class OutfitViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val outfitPrevIv: ImageView = itemView.findViewById(R.id.outfitPrevIv)
        private val deleteOutfitBtn: ImageButton = itemView.findViewById(R.id.deleteOutfitBtn)

        fun bind(outfit: OutfitModel) {
            Glide.with(itemView.context).load(outfit.outfitPath).into(outfitPrevIv)

            deleteOutfitBtn.setOnClickListener {
                showDeleteOutfitConfirmationDialog(outfit)
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ViewOutfitActivity::class.java)
                intent.putExtra(ViewOutfitActivity.SELECTED_DATE_KEY, UserModel.DATE_FORMAT.format(outfit.date))
                intent.putExtra(ViewOutfitActivity.CURRENT_USER_KEY, outfit.user_uuid)
                intent.putExtra(ViewOutfitActivity.OUTFIT_PATH_KEY, outfit.outfitPath)
                itemView.context.startActivity(intent)
            }
        }

        private fun showDeleteOutfitConfirmationDialog(outfit: OutfitModel) {
            val builder = AlertDialog.Builder(itemView.context)

            builder.setTitle("Confirm Outfit Deletion")
                .setMessage("Are you sure you want to delete this outfit?")
                .setPositiveButton("Delete") { dialog, which ->
                    // Notify the listener about the deletion
                    (itemView.context as? OnOutfitDeleteListener)?.onOutfitDeleted(outfit)
                }
                .setNegativeButton("Cancel") { dialog, which ->
                }
                .show()
        }
    }

    interface OnOutfitDeleteListener {
        fun onOutfitDeleted(outfit: OutfitModel)
    }
}
