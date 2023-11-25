package com.mobdeve.s12.aiwear.models

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.mobdeve.s12.aiwear.R
import java.util.Date

class OutfitModel(
    var outfit_id: String = "",
    val user_uuid: String = "",
    var date: Date = Date(),
    var clothes: MutableList<BitmapData> = mutableListOf(),
    var outfitBitmap: Bitmap? = null,
    var outfitPath: String = ""
) {

    constructor() : this("", "", Date(), mutableListOf(), null, "")

    // variable for a snapshot of outfitcanvas
    var delimitted_clothes = ""

    fun formatClothesId(): String {
        var delimitted_clothes = ""
        if(clothes != null){
            for (c in clothes!!) {
                delimitted_clothes = delimitted_clothes + c.id + "|"
            }
        }
        return delimitted_clothes
    }

    fun parseClothesId() {
        var ids = this.delimitted_clothes.split("|")
        // query db with IDs
        // store into this.clothes
    }
}

data class BitmapData(
    val id: String,
    val bitmap: Bitmap?,
    val matrix: Matrix,
    var left: Float,
    var top: Float) {

    constructor(id: String, left: Float, top: Float): this(id, null, Matrix(), left, top)
    constructor(): this("", null, Matrix(), 0f, 0f)
}

