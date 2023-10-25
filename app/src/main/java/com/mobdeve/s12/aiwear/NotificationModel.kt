package com.mobdeve.s12.aiwear

import java.util.Date

data class NotificationModel(
    var type: String = "default",
    var headline: String,
    var body: String,
    val date: Date,
    var read: Boolean
)
