package com.mobdeve.s12.aiwear.models

import com.mobdeve.s12.aiwear.activities.ForumActivity
import java.text.SimpleDateFormat
import java.util.Date

data class ForumPostModel(
    var post_id: String = "",
    var title: String,
    var content: String,
    var photoUrl: String = "",
    var created_by: String = "", // user uuid, not username
    var created_at: Date = Date(),
    var last_modified_at: Date = Date(),
    var likes: Int = 0,
    var commentsCount: Int = 0,
    var comments: ArrayList<ForumCommentModel> = ArrayList()
) {

    companion object {

        const val POST_ID_KEY = "POST_ID"
        const val POST_TITLE_KEY = "POST_TITLE"
        const val POST_CONTENT_KEY = "POST_KEY"
        const val POST_PHOTOURL_KEY = "POST_PHOTOURL"
        const val POST_CREATED_BY_KEY = "POST_USER_ID"
        const val POST_CREATED_AT_KEY = "POST_CREATED_AT"
        const val POST_LAST_MODIFIED_KEY = "POST_LAST_MODIFIED"
        const val POST_LIKES_KEY = "POST_LIKES"
        const val POST_COMMENTS_COUNT_KEY = "POST_COMMENTS_COUNT"
        const val POST_COMMENTS_KEY = "POST_COMMENTS"

        const val USER_NAME_KEY = "POST_USER_NAME"
        const val USER_PHOTOURL_KEY = "POST_USER_PHOTOURL"
        val DATE_FORMAT = SimpleDateFormat("MMM dd, yyyy, h:mm a")

    }

    constructor(
        title: String,
        content: String,
        created_by: String,
        created_at: Date,
        last_modified_at: Date,
        likes: Int = 0,
        comments: ArrayList<ForumCommentModel> = ArrayList()
    ) : this("", title, content, "", created_by, created_at, last_modified_at, likes, comments.size, comments)

    constructor(): this("", "title", "content", "", "", Date(), Date(), 0, 0, ArrayList())
}
