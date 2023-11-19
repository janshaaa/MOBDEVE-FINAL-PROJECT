package com.mobdeve.s12.aiwear.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.fragments.BaseClothesFragment
import com.mobdeve.s12.aiwear.models.ClothesItem
import java.io.File


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
        val clothesItem = clothesListFiltered[position]
        holder.bind(clothesItem, position, onItemClickListener)

        val imageView = holder.imageView

        if (clothesItem.imagePath != null) {
            Glide.with(context)
                .load(File(clothesItem.imagePath))
                .into(imageView)
        } else {
//            imageView.setImageResource(clothesItem.imageResId ?: R.drawable.imageerror)
            imageView.setImageResource(R.drawable.imageerror)
        }

        Log.d("ADAPTER ONBIND", "ADDING ${clothesItem.name}")
    }

    override fun getItemCount(): Int = clothesListFiltered.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.clothes_image)
        private val nameTextView: TextView = view.findViewById(R.id.clothes_name)
        private val deleteButton: ImageButton = view.findViewById(R.id.delete_button)

        fun bind(clothesItem: ClothesItem, position: Int, listener: OnItemClickListener?) {
            nameTextView.text = clothesItem.name

            itemView.setOnClickListener {
                listener?.onItemClick(position)
            }

            deleteButton.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // Remove the item from the filtered/displayed list
                    Log.d("ADAPTER CLASS CLOTHESLIST REMOVE:", "${clothesListFiltered[currentPosition].name}")
                    val itemToDelete = clothesListFiltered.removeAt(currentPosition)
                    notifyItemRemoved(currentPosition)
                    notifyItemRangeChanged(currentPosition, clothesListFiltered.size)

                    // Also remove the item from the full list
                    val check =clothesList.remove(itemToDelete)
                    if (check) {
                        Log.d("ADAPTER AFTER", "CLOTHESLIST REMOVED SUCCESS")
                    }

                    Log.d("ADAPTER CLASS CLOTHESLIST REMOVE:", "${clothesList[currentPosition].name}")
                    notifyItemRemoved(currentPosition)


                    // find and remove the item from the allClothesList
                    allClothesList.indexOfFirst { it.name == itemToDelete.name }.let { indexInAllList ->
                        if (indexInAllList != -1) {
                            Log.d("ADAPTER CLASS CLOTHESLIST REMOVE:", "${allClothesList[indexInAllList].name}")
                            val removedItem = allClothesList.removeAt(indexInAllList)
                            if (check) {
                                Log.d("ADAPTER AFTER", "ALLCLOTHESLIST REMOVED SUCCESS: ${removedItem.name}")
                                Log.d("ADAPTER AFTER CHECK", "ALLCLOTHESLIST REMOVED SUCCESS: ${allClothesList[indexInAllList].name}")
                            }
                            notifyItemRemoved(indexInAllList)
                            callback.saveClothesList(allClothesList)

                        }

                        Log.d("ADAPTER AFTER", "NUMBER OF ITEMS IN ALLCLOTHESLIST: ${allClothesList.size}")

                    }
                }
            }

        }
    }

    fun addItem(item: ClothesItem) {
        allClothesList.add(item)
        notifyItemInserted(allClothesList.size - 1)
        callback.saveClothesList(allClothesList)
    }
    fun filter(query: String) {
        clothesListFiltered = if (query.isEmpty()) {
            ArrayList(clothesList)
        } else {
            val filteredList = clothesList.filter {
                it.name?.contains(query, ignoreCase = true) == true ||
                        it.size?.contains(query, ignoreCase = true) == true ||
                        it.brand?.contains(query, ignoreCase = true) == true ||
                        it.material?.contains(query, ignoreCase = true) == true ||
                        it.color?.contains(query, ignoreCase = true) == true
            }
            ArrayList(filteredList)
        }
        notifyDataSetChanged()
    }

}




