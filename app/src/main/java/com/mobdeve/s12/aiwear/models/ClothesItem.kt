package com.mobdeve.s12.aiwear.models

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
//    constructor(clothes_id: String, user_uuid: String, name: String, imageResId: Int, imagePath: String, category: String, size: String, color: String, material: String, brand: String) {
//        this.clothes_id = clothes_id
//        this.user_uuid = user_uuid
//        this.name = name
//        this.imageResId = imageResId
//        this.imagePath = imagePath
//        this.category = category
//        this.size = size
//        this.color = color
//        this.material = material
//        this.brand = brand
//    }
//    constructor(user_uuid: String, name: String, imageResId: Int, category: String, size: String, color: String, material: String, brand: String) {
//        this.user_uuid = user_uuid
//        this.name = name
//        this.imageResId = imageResId
//        this.category = category
//        this.size = size
//        this.color = color
//        this.material = material
//        this.brand = brand
//    }
//
//    constructor(user_uuid: String, name: String, imageResId: Int, imagePath: String, category: String, size: String, color: String, material: String, brand: String) {
//        this.user_uuid = user_uuid
//        this.name = name
//        this.imageResId = imageResId
//        this.imagePath = imagePath
//        this.category = category
//        this.size = size
//        this.color = color
//        this.material = material
//        this.brand = brand
//    }

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