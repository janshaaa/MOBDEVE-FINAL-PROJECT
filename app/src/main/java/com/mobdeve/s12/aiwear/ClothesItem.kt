package com.mobdeve.s12.aiwear

import java.io.Serializable

class ClothesItem : Serializable {
    var name:String? = null
    var image:Int? = null
    var category: String? = null
    var size: String? = null
    var color: String? = null
    var material: String? = null
    var brand: String? = null

    constructor(name: String, image: Int, category: String, size: String, color: String, material: String, brand: String){
        this.name = name
        this.image = image
        this.category = category
        this.size = size
        this.color = color
        this.material = material
        this.brand = brand
    }
}