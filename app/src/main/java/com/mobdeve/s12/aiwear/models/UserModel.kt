package com.mobdeve.s12.aiwear.models

import com.mobdeve.s12.aiwear.R
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date

data class UserModel(
    val uuid: String,
    var userName: String,
    var displayName: String,
    var bio: String,
    var gender: String,
    var birthday: Date,
    var photoUrl: String,
    var likedPosts: ArrayList<String> = ArrayList()
) {

    companion object {
        private const val DEFAULT_UUID = "0"
        val GENDER_OPTIONS = arrayOf("Female", "Male", "Non-Binary", "Rather not say")
        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val PHOTO_PLACEHOLDER = R.drawable.profile_placeholder
    }

    constructor(userName: String, displayName: String, bio: String, gender: String, birthday: Date, photoUrl: String) : this(
        DEFAULT_UUID, userName, displayName, bio, gender, birthday, photoUrl)
    constructor() : this(DEFAULT_UUID, "username", "User", "", "Rather not say", Date(), "")

    fun addToLikedPosts(post_id: String) {
        likedPosts.add(post_id)
    }
}
