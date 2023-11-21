package com.mobdeve.s12.aiwear.models

import java.io.Serializable

class ClothesItem : Serializable {
    var clothes_id: String = "-1"
    var name: String? = null
    var imageResId: Int? = null // for drawable resources
    var imagePath: String? = null // for images from camera
    var category: String? = null
    var size: String? = null
    var color: String? = null
    var material: String? = null
    var brand: String? = null

    constructor(name: String, imageResId: Int, category: String, size: String, color: String, material: String, brand: String) {
        this.name = name
        this.imageResId = imageResId
        this.category = category
        this.size = size
        this.color = color
        this.material = material
        this.brand = brand
    }

    constructor(name: String, imageResId: Int?, imagePath: String?, category: String, size: String, color: String, material: String, brand: String) {
        this.name = name
        this.imageResId = imageResId
        this.imagePath = imagePath
        this.category = category
        this.size = size
        this.color = color
        this.material = material
        this.brand = brand
    }

    constructor(name: String, imagePath: String, category: String, size: String, color: String, material: String, brand: String) {
        this.name = name
        this.imagePath = imagePath
        this.category = category
        this.size = size
        this.color = color
        this.material = material
        this.brand = brand
    }

}