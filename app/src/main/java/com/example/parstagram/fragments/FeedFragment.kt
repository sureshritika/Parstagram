package com.example.parstagram.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.parstagram.*
import com.parse.FindCallback
import com.parse.GetCallback
import com.parse.ParseException
import com.parse.ParseQuery
import java.util.*

open class FeedFragment : Fragment() {

    lateinit var swipeContainer : SwipeRefreshLayout
    lateinit var feedProgress : ProgressBar
    lateinit var postsRV : RecyclerView
    lateinit var loadingMoreProgress : ProgressBar

    open lateinit var adapter : Any
    var posts : MutableList<Post> = mutableListOf()
    lateinit var scrollListener : EndlessRecyclerViewScrollListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (fragmentManager?.findFragmentById(R.id.id_flContainer) is FeedFragment)
            (activity as MainActivity).supportActionBar?.setCustomView(R.layout.header_ig)

        swipeContainer = view.findViewById(R.id.id_swipeContainer)
        feedProgress = view.findViewById(R.id.id_feedProgress)
        postsRV = view.findViewById(R.id.id_postsRV)
        loadingMoreProgress = view.findViewById(R.id.id_loadingMoreProgress)

        adapter = PostsAdapter(posts , requireContext() , activity as MainActivity)
        postsRV.adapter = adapter as PostsAdapter
        postsRV.layoutManager = LinearLayoutManager(requireContext())

        swipeContainer.setColorSchemeResources(R.color.ig_yellow , R.color.ig_pink , R.color.ig_purple , R.color.ig_dark_blue);
        swipeContainer.setOnRefreshListener {
            Log.i("RITIKA" , "refreshing timeline")
            queryPosts()
        }

        scrollListener = object : EndlessRecyclerViewScrollListener(postsRV.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Log.d("RITIKA" , "loading more")
                loadingMoreProgress.visibility = View.VISIBLE
                Handler().postDelayed({
                    queryMorePosts()
                } , 1000)

            }
        }
        postsRV.addOnScrollListener(scrollListener)

        feedProgress.visibility = View.VISIBLE
        queryPosts()
    }

    open fun queryPosts() {
        Log.d("RITIKA" , "feed queryPosts")
        // Specify which class to query
        val query : ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all Post objects
        query.include(Post.KEY_USER)
        query.addDescendingOrder("createdAt")
        query.limit = MainActivity.count

        query.findInBackground(object : FindCallback<Post> {
            override fun done(postsList: MutableList<Post>?, e: ParseException?) {
                feedProgress.visibility = View.GONE
                if (e != null) {
                    Log.e("RITIKA" , "queryPosts failure ${e}")
                } else {
                    if (postsList != null) {
                        if (postsList.size != 0) {
                            Log.e("RITIKA", "queryPosts success")
                            posts.clear()
                            posts.addAll(postsList)
                            (adapter as PostsAdapter).notifyDataSetChanged()
                            swipeContainer.isRefreshing = false
                            lastPostCreatedDate = postsList.get(postsList.size - 1).createdAt as Date
                        }
                    }
                }
            }
        })
    }

    open fun queryMorePosts() {
        Log.d("RITIKA" , "feed queryMorePosts")
        val query : ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        query.whereLessThan("createdAt" , lastPostCreatedDate)
        query.addDescendingOrder("createdAt")
        query.limit = 10

        query.findInBackground(object : FindCallback<Post> {
            override fun done(postsList: MutableList<Post>?, e: ParseException?) {
                loadingMoreProgress.visibility = View.GONE
                if (e != null) {
                    Log.e("RITIKA" , "queryMorePosts failure ${e}")
                } else {
                    if (postsList != null) {
                        if (postsList.size != 0) {
                            Log.e("RITIKA", "queryMorePosts success $postsList")
                            posts.addAll(postsList)
                            (adapter as PostsAdapter).notifyDataSetChanged()
                            swipeContainer.setRefreshing(false);
                            scrollListener.resetState();
                            lastPostCreatedDate = postsList.get(postsList.size - 1).createdAt as Date
                            MainActivity.count = posts.size
                        }
                        else {
                            Log.d("RITIKA" , "nothing to get")
                        }
                    }
                }
            }
        })
    }

    companion object {
        lateinit var lastPostCreatedDate : Date
    }

}