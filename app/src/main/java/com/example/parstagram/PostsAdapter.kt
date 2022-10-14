package com.example.parstagram

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class PostsAdapter(private var posts : List<Post>, private val context : Context) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_post , parent , false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post : Post = posts.get(position)
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun addAll(postList : List<Post>) {
        posts += postList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage = itemView.findViewById<ImageView>(R.id.id_profileImg)
        private val username = itemView.findViewById<TextView>(R.id.id_username)
        private val more = itemView.findViewById<ImageButton>(R.id.id_more)
        private val postImg = itemView.findViewById<ImageView>(R.id.id_postImg)
        private val like = itemView.findViewById<ImageButton>(R.id.id_like)
        private val comment = itemView.findViewById<ImageButton>(R.id.id_comment)
        private val share = itemView.findViewById<ImageButton>(R.id.id_share)
        private val save = itemView.findViewById<ImageButton>(R.id.id_save)
        private val caption = itemView.findViewById<TextView>(R.id.id_caption)

        fun bind(post : Post) {
            //Log.d("RITIKA" , "posts adapter ${post}")
            Glide.with(context).load(R.drawable.rs).apply(RequestOptions.circleCropTransform()).into(profileImage)
            username.text = post.getUser()?.username
            Glide.with(context).load(post.getImage()?.url).placeholder(R.drawable.loading).thumbnail(Glide.with(context).load(R.drawable.loading)).into(postImg)
            caption.text = post.getDescription()
        }
    }

}