package com.example.parstagram

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class CommentsAdapter(private var comments : List<Comment>, private val context : Context) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_comment , parent , false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment : Comment = comments.get(position)
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage = itemView.findViewById<ImageView>(R.id.id_profileImg)
        private val username = itemView.findViewById<TextView>(R.id.id_username)
        private val commentTxt = itemView.findViewById<TextView>(R.id.id_comment)

        fun bind(comment : Comment) {
            Glide.with(itemView.context).load(comment.getCommenter()?.getParseFile("profileImage")?.url).placeholder(R.drawable.loading).thumbnail(Glide.with(itemView.context).load(R.drawable.loading)).apply(RequestOptions.circleCropTransform()).into(profileImage)
            username.text = comment.getCommenter()?.username
            commentTxt.text = comment.getComment()
        }
    }
}