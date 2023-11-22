package com.mobdeve.s12.aiwear.models

import java.util.Date

data class ForumCommentModel(
    var comment_id: String,
    var post_id: String,
    var content: String,
    var created_by: String,
    var create_at: Date
) {

}
