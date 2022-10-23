package com.example.parstagram

import android.util.Log
import com.parse.*
import org.json.JSONArray

@ParseClassName("Post")
class Post : ParseObject() {

    fun getDescription() : String? {
        return getString(KEY_DESCRIPTION)
    }

    fun setDescription(description : String) {
        put(KEY_DESCRIPTION , description)
    }

    fun getImage() : ParseFile? {
        return getParseFile(KEY_IMAGE)
    }

    fun setImage(parseFile: ParseFile) {
        put(KEY_IMAGE , parseFile)
    }

    fun getUser() : ParseUser? {
        return getParseUser(KEY_USER)
    }

    fun setUser(user : ParseUser) {
        put(KEY_USER , user)
    }

    fun getProfileImage() : ParseFile? {
        return getUser()?.getParseFile(KEY_PROF_IMG)
    }

    fun addLike() {
        put(KEY_LIKES , getLikes()+1)
    }

    fun removeLike() {
        put(KEY_LIKES , getLikes()-1)
    }

    fun getLikes() : Int {
        return getInt(KEY_LIKES)
    }

    fun addLiker() {
        put(KEY_LIKERS , mutableListOf(ParseUser.getCurrentUser()))
    }

    fun removeLiker() {
        removeAll(KEY_LIKERS , mutableListOf(ParseUser.getCurrentUser()))
    }

    fun getLikers() : List<ParseUser>? {
        return getList(KEY_LIKERS)
    }

    fun getFormattedTimeStamp() : String {
        return TimeFormatter.getTimeDifference(createdAt.toString())
    }

    companion object {
        const val KEY_DESCRIPTION = "description"
        const val KEY_IMAGE = "image"
        const val KEY_USER = "user"
        const val KEY_PROF_IMG = "profileImage"
        const val KEY_LIKES = "likes"
        const val KEY_LIKERS = "likers"

    }

}