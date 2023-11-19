package com.mobdeve.s12.aiwear.models

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date

data class UserModel(
    val uuid: String,
    var userName: String,
    var displayName: String,
    var bio: String,
    var gender: String,
    var birthday: Date
) {
    companion object {
        private const val DEFAULT_UUID = "0"
        val GENDER_OPTIONS = arrayOf("Female", "Male", "Non-Binary", "Rather not say")
        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
    }

    constructor(userName: String, displayName: String, bio: String, gender: String, birthday: Date) : this(
        DEFAULT_UUID, userName, displayName, bio, gender, birthday)
    constructor() : this(DEFAULT_UUID, "username", "User", "", "Rather not say", Date())
}
