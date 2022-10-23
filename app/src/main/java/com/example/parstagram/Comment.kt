package com.example.parstagram

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("Comment")
class Comment : ParseObject() {

    fun getComment() : String? {
        return getString(KEY_COMMENT)
    }

    fun setComment(comment : String) {
        put(KEY_COMMENT , comment)
    }

    fun getCommenter() : ParseUser? {
        return getParseUser(KEY_COMMENTER)
    }

    fun setCommenter(commenter : ParseUser) {
        put(KEY_COMMENTER , commenter)
    }

    fun getPost() : Post? {
        return getParseObject(KEY_POST) as Post
    }

    fun setPost(post : Post) {
        put(KEY_POST , post)
    }

    companion object {
        const val KEY_COMMENT = "comment"
        const val KEY_COMMENTER = "commenter"
        const val KEY_POST = "post"

    }

}