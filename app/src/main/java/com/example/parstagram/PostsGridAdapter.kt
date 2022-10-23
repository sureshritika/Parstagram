package com.example.parstagram

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.parstagram.fragments.PostDetailFragment
import com.example.parstagram.fragments.ProfileFragment


class PostsGridAdapter(private var posts : List<Post>, private val context : Context , private val activity: FragmentActivity) : RecyclerView.Adapter<PostsGridAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_post_grid , parent , false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post : Post = posts.get(position)
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val postImg = itemView.findViewById<ImageView>(R.id.id_postImg)

        fun bind(post : Post) {
            Glide.with(itemView.context).load(post.getImage()?.url).placeholder(R.drawable.loading).thumbnail(Glide.with(itemView.context).load(R.drawable.loading)).into(postImg)

            itemView.setOnClickListener {
                val post = posts.get(adapterPosition)
                val postDetailFragment = PostDetailFragment()
                val bundle = Bundle()
                bundle.putParcelable("POST_EXTRA" , post)
                postDetailFragment.arguments = bundle
                activity.supportFragmentManager.beginTransaction().replace(R.id.id_flContainer, postDetailFragment).addToBackStack("FeedFragment()").commit()
            }
        }




    }

}