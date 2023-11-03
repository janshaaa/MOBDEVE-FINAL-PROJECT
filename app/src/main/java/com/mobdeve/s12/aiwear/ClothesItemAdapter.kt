package com.mobdeve.s12.aiwear

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ClothesItemAdapter(
    private val clothesList: MutableList<ClothesItem>,
    private val allClothesList: MutableList<ClothesItem>,
    private val context: Context,
    private val callback: BaseClothesFragment
) : RecyclerView.Adapter<ClothesItemAdapter.ViewHolder>() {

    private var clothesListFiltered = ArrayList(clothesList)

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_clothes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clothesItem = clothesList[position]
        holder.bind(clothesItem, position, onItemClickListener)
    }

    override fun getItemCount(): Int = clothesList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.clothes_image)
        private val nameTextView: TextView = view.findViewById(R.id.clothes_name)
        private val deleteButton: ImageButton = view.findViewById(R.id.delete_button)

        // Method to bind data to the ViewHolder
        fun bind(clothesItem: ClothesItem, position: Int, listener: OnItemClickListener?) {
            clothesItem.image?.let {
                imageView.setImageResource(it)
            }
            nameTextView.text = clothesItem.name

            // Set the click listener
            itemView.setOnClickListener {
                listener?.onItemClick(position)
            }

            deleteButton.setOnClickListener {
                val currentPosition = adapterPosition
                Log.d("DeleteClick", "Position: $currentPosition, Item: ${clothesList[currentPosition].name}")
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // Remove the item from the filtered/displayed list
                    val itemToDelete = clothesList.removeAt(currentPosition)
                    notifyItemRemoved(currentPosition)
                    notifyItemRangeChanged(currentPosition, clothesList.size)

                    // Find and remove the item from the allClothesList
                    allClothesList.indexOfFirst { it.name == itemToDelete.name }.let { indexInAllList ->
                        if (indexInAllList != -1) {
                            allClothesList.removeAt(indexInAllList)
                            callback.saveClothesList(allClothesList) // Persist changes
                        }
                    }
                }
            }

        }
    }

    fun filter(query: String) {
        clothesListFiltered = if (query.isEmpty()) ({
            clothesList
        }) as ArrayList<ClothesItem> else {
            val filteredList = clothesList.filter {
                it.name?.contains(query, ignoreCase = true) ?: false
            }
            ArrayList(filteredList)
        }
        notifyDataSetChanged()
    }

    fun updateData(newList: List<ClothesItem>) {
        clothesList.clear()
        clothesList.addAll(newList)
        notifyDataSetChanged()
    }

}




