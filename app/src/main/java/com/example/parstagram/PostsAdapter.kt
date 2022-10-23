package com.example.parstagram

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.net.ParseException
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.parstagram.fragments.PostDetailFragment
import com.example.parstagram.fragments.ProfileFragment
import com.parse.*


class PostsAdapter(private var posts : List<Post>, private val context : Context , private val activity: FragmentActivity) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    lateinit var post : Post

    lateinit var dialog : AlertDialog
    lateinit var commentEdit : EditText
    lateinit var postBtn : Button
    lateinit var postProgress : ProgressBar
    lateinit var cancelBtn : ImageButton

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_post , parent , false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        post = posts.get(position)
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val header = itemView.findViewById<RelativeLayout>(R.id.id_header)
        private val profileImage = itemView.findViewById<ImageView>(R.id.id_profileImg)
        private val username = itemView.findViewById<TextView>(R.id.id_username)
        private val createdAt = itemView.findViewById<TextView>(R.id.id_createdAt)
        private val postImg = itemView.findViewById<ImageView>(R.id.id_postImg)
        private val commentBtn = itemView.findViewById<ImageButton>(R.id.id_commentBtn)
        private val likeBtn = itemView.findViewById<ImageButton>(R.id.id_likeBtn)
        private val likes = itemView.findViewById<TextView>(R.id.id_likes)
        private val shareBtn = itemView.findViewById<ImageButton>(R.id.id_shareBtn)
        private val saveBtn = itemView.findViewById<ImageButton>(R.id.id_saveBtn)
        private val caption = itemView.findViewById<TextView>(R.id.id_caption)

        fun bind(post : Post) {

            Glide.with(itemView.context).load(post.getProfileImage()?.url).placeholder(R.drawable.loading).thumbnail(Glide.with(itemView.context).load(R.drawable.loading)).apply(RequestOptions.circleCropTransform()).into(profileImage)
            username.text = post.getUser()?.username
            createdAt.text = post.getFormattedTimeStamp()
            Glide.with(itemView.context).load(post.getImage()?.url).placeholder(R.drawable.loading).thumbnail(Glide.with(itemView.context).load(R.drawable.loading)).into(postImg)

            Log.d("RITIKA" , "post ${post.getUser()?.username} caption ${post.getDescription()}")
            var match = false
            if (post.getLikers() != null) {
                for (liker in post.getLikers()!!) {
                    if (liker.objectId == ParseUser.getCurrentUser().objectId) {
                        match = true
                        Log.d("RITIKA" , "match")
                    }

                }
            }
            likeBtn.isSelected = match == true

            likes.text = "${post.getLikes()} likes"
            caption.text = post.getDescription()

            likeBtn.setOnClickListener {
                likeBtnClicked(likeBtn , post, likes)
            }

            header.setOnClickListener {
                activity.supportFragmentManager.beginTransaction().replace(R.id.id_flContainer, ProfileFragment(post.getUser()!!)).addToBackStack("FeedFragment()").commit()
            }

            commentBtn.setOnClickListener {
                val view = LayoutInflater.from(itemView.context).inflate(R.layout.fragment_comment, null)
                val builder = AlertDialog.Builder(itemView.context).setView(view).setCancelable(false)
                dialog = builder.show()

                commentEdit = view.findViewById(R.id.id_commentEdit)
                postBtn = view.findViewById(R.id.id_postBtn)
                postProgress = view.findViewById(R.id.id_postProgress)
                cancelBtn = view.findViewById(R.id.id_cancelBtn)

                postBtn.isEnabled = false
                postBtn.setBackgroundColor(Color.GRAY)

                commentEdit.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                        if (p0.isEmpty()) {
                            postBtn.isEnabled = false
                            postBtn.setBackgroundColor(Color.GRAY)
                        }
                        else
                        {
                            postBtn.isEnabled = true
                            postBtn.setBackgroundColor(Color.WHITE)
                        }
                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }

                })

                cancelBtn.setOnClickListener {
                    Log.d("RITIKA" , "cancelBtn clicked")
                    dialog.hide()
                }

                postBtn.setOnClickListener {
                    progressOn()
                    val comment_str = commentEdit.text.toString()
                    val comment = Comment()
                    comment.setComment(comment_str)
                    comment.setCommenter(ParseUser.getCurrentUser())
                    comment.setPost(post)
                    comment.saveInBackground(object : SaveCallback {
                        override fun done(e: com.parse.ParseException?) {
                            progressOff()
                            if (e == null) {
                                Log.i("RITIKA" , "postBtn success")
                                dialog.hide()
                            } else {
                                Log.e("RITIKA" , "postBtn failure ${e}")
                                e.printStackTrace()
                            }
                        }
                    })
                }

            }

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

    fun progressOn() {
        postProgress.visibility = View.VISIBLE
        dialog.findViewById<RelativeLayout>(R.id.id_commentLayout)?.children?.forEach {
            if (it.id != postProgress.id) {
                it.isEnabled = false
                it.alpha = 0.25F
            }
        }
    }

    fun progressOff() {
        postProgress.visibility = View.GONE
        dialog.findViewById<RelativeLayout>(R.id.id_commentLayout)?.children?.forEach {
            if (it.id != postProgress.id) {
                it.isEnabled = true
                it.alpha = 1F
            }
        }
    }

    fun likeBtnClicked(likeBtn : ImageButton , post : Post , likes : TextView) {
        likeBtn.isSelected = !likeBtn.isSelected
        Log.d("RITIKA" , "one selected ${likeBtn.isSelected}")

        if (likeBtn.isSelected) {
            val query : ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
            query.getInBackground(post.objectId, object : GetCallback<Post?> {
                override fun done(`object`: Post?, e: com.parse.ParseException?) {
                    if (e == null) {
                        Log.i("RITIKA" , "likeBtn add get success")
                        post.addLike()
                        post.addLiker()
                        post.saveInBackground() { e ->
                            if (e != null) {
                                Log.e("RITIKA" , "likeBtn add save failure ${e}")
                                e.printStackTrace()
                            } else {
                                Log.i("RITIKA" , "likeBtn add save success")
                                likes.text = "${post.getLikes()} likes"
                            }
                        }
                    } else {
                        Log.e("RITIKA" , "likeBtn add get failure ${e}")
                        e.printStackTrace()
                    }
                }
            })
        }

        if (likeBtn.isSelected == false) {
            val query : ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
            query.getInBackground(post.objectId, object : GetCallback<Post?> {
                override fun done(`object`: Post?, e: com.parse.ParseException?) {
                    if (e == null) {
                        Log.i("RITIKA" , "likeBtn remove get success")
                        post.removeLike()
                        post.removeLiker()
                        post.saveInBackground() { e ->
                            if (e != null) {
                                Log.e("RITIKA" , "likeBtn remove save failure ${e}")
                                e.printStackTrace()
                            } else {
                                Log.i("RITIKA" , "likeBtn remove save success")
                                likes.text = "${post.getLikes()} likes"
                            }
                        }
                    } else {
                        Log.e("RITIKA" , "likeBtn remove get failure ${e}")
                        e.printStackTrace()
                    }
                }
            })
        }
    }

}