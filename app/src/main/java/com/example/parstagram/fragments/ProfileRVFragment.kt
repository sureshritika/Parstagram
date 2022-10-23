package com.example.parstagram.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parstagram.*
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser
import org.w3c.dom.Text
import java.util.*

class ProfileRVFragment(val user : ParseUser) : FeedFragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (user != ParseUser.getCurrentUser())
        {
            adapter = PostsGridAdapter(posts , requireContext() , activity as MainActivity)
            postsRV.adapter = adapter as PostsGridAdapter
            postsRV.layoutManager = GridLayoutManager(requireContext() , 3)
        }
        else {
            adapter = PostsAdapter(posts , requireContext() , activity as MainActivity)
            postsRV.adapter = adapter as PostsAdapter
            postsRV.layoutManager = LinearLayoutManager(requireContext())
        }

    }

    override fun queryPosts() {
        Log.d("RITIKA" , "profile queryPosts")
        // Specify which class to query
        val query : ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all Post objects
        query.include(Post.KEY_USER)
        // Only return posts from currently signed in user
        query.whereEqualTo(Post.KEY_USER , user)
        query.addDescendingOrder("createdAt")

        query.findInBackground(object : FindCallback<Post> {
            override fun done(postsList: MutableList<Post>?, e: ParseException?) {
                feedProgress.visibility = View.GONE
                if (e != null) {
                    Log.e("RITIKA" , "queryPosts failure ${e}")
                } else {
                    if (postsList != null) {
                        posts.addAll(postsList)
                        if (user == ParseUser.getCurrentUser())
                            (adapter as PostsAdapter).notifyDataSetChanged()
                        else
                            (adapter as PostsGridAdapter).notifyDataSetChanged()
                        swipeContainer.isRefreshing = false
                        for (post in posts) {
                            //Log.i("RITIKA" , "profileuser: ${post.getUser()?.username} ; image: ${post.getImage()} ; comment: ${post.getDescription()}")
                        }
                    }
                }
            }
        })
    }
}