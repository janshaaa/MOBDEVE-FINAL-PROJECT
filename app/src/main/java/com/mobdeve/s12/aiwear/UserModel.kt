package com.mobdeve.s12.aiwear

import java.util.Date

data class UserModel(
    val uid: String,
    var displayName: String,
    var bio: String,
    var gender: String,
    var birthday: Date
)
