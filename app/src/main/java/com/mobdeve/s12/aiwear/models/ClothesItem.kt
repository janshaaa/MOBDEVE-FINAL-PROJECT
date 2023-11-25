package com.mobdeve.s12.aiwear.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.transition.Transition
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.mobdeve.s12.aiwear.R
import java.io.Serializable

class ClothesItem : Serializable {
    var clothes_id: String = ""
    lateinit var  user_uuid: String
    lateinit var name: String
//    var imageResId: Int = 0 // for drawable resources
    var imagePath: String = "" // for images from camera
    var category: String = ""
    var size: String = ""
    var color: String = ""
    var material: String = ""
    var brand: String = ""

    constructor()

    constructor(user_uuid: String, name: String, imagePath: String, category: String, size: String, color: String, material: String, brand: String) {
        this.user_uuid = user_uuid
        this.name = name
        this.imagePath = imagePath
        this.category = category
        this.size = size
        this.color = color
        this.material = material
        this.brand = brand
    }
}