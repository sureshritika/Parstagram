package com.example.parstagram.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.parstagram.*
import com.example.parstagram.R
import com.parse.*


class PostDetailFragment : Fragment() {

    lateinit var profileImage : ImageView
    lateinit var username : TextView
    lateinit var createdAt : TextView
    lateinit var postImg : ImageView
    lateinit var likeBtn : ImageButton
    lateinit var commentBtn : ImageButton
    lateinit var likes : TextView
    lateinit var caption : TextView
    lateinit var commentsRV : RecyclerView
    lateinit var loadingProgress : ProgressBar

    lateinit var dialog : AlertDialog
    lateinit var commentEdit : EditText
    lateinit var postBtn : Button
    lateinit var postProgress : ProgressBar
    lateinit var cancelBtn : ImageButton

    var post = Post()
    lateinit var adapter : CommentsAdapter
    var comments : MutableList<Comment> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        post = arguments?.get("POST_EXTRA") as Post
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_post_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar?.setCustomView(R.layout.header_post)

        profileImage = view.findViewById(R.id.id_profileImg)
        username = view.findViewById(R.id.id_username)
        createdAt = view.findViewById(R.id.id_createdAt)
        postImg = view.findViewById(R.id.id_postImg)
        likeBtn = view.findViewById(R.id.id_likeBtn)
        commentBtn = view.findViewById(R.id.id_commentBtn)
        likes = view.findViewById(R.id.id_likes)
        caption = view.findViewById(R.id.id_caption)
        commentsRV = view.findViewById(R.id.id_commentsRV)
        loadingProgress = view.findViewById(R.id.id_loadingProgress)

        adapter = CommentsAdapter(comments , requireContext())
        commentsRV.adapter = adapter
        commentsRV.layoutManager = LinearLayoutManager(requireContext())

        Glide.with(requireContext()).load(post?.getProfileImage()?.url).placeholder(R.drawable.loading).thumbnail(Glide.with(requireContext()).load(R.drawable.loading)).apply(RequestOptions.circleCropTransform()).into(profileImage)
        username.text = post?.getUser()?.username
        createdAt.text = post?.getFormattedTimeStamp()
        Glide.with(requireContext()).load(post?.getImage()?.url).placeholder(R.drawable.loading).thumbnail(Glide.with(requireContext()).load(R.drawable.loading)).into(postImg)
        likes.text = "${post.getLikes()} likes"
        caption.text = post?.getDescription()

        post.getLikers()?.forEach {
            if (it.objectId == ParseUser.getCurrentUser().objectId) {
                likeBtn.isSelected = true
            }
        }

        queryComments()

        likeBtn.setOnClickListener {
            likeBtnClicked(likeBtn , post, likes)
        }

        commentBtn.setOnClickListener {
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_comment, null)
            val builder = AlertDialog.Builder(requireContext()).setView(view).setCancelable(false)
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
                    override fun done(e: ParseException?) {
                        progressOff()
                        if (e == null) {
                            Log.i("RITIKA" , "postBtn success")
                            dialog.hide()
                            queryComments()
                        } else {
                            Log.e("RITIKA" , "postBtn failure ${e}")
                            e.printStackTrace()
                        }
                    }
                })
            }

        }

        requireActivity().findViewById<ImageButton>(R.id.id_back).setOnClickListener{
            fragmentManager?.popBackStack()
        }
    }

    fun queryComments() {
        Log.d("RITIKA" , "queryComments")
        val query : ParseQuery<Comment> = ParseQuery.getQuery(Comment::class.java)
        query.include(Comment.KEY_COMMENTER)
        query.include(Comment.KEY_POST)
        query.whereEqualTo(Comment.KEY_POST, post);
        query.addDescendingOrder("createdAt")
        query.findInBackground(object : FindCallback<Comment> {
            override fun done(commentList: MutableList<Comment>?, e: ParseException?) {
                loadingProgress.visibility = View.GONE
                if (e != null) {
                    Log.e("RITIKA" , "queryComments failure ${e}")
                } else {
                    if (commentList != null) {
                        if (commentList.size != 0) {
                            Log.e("RITIKA", "queryComments success")
                            comments.clear()
                            comments.addAll(commentList)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }

        })
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