package com.example.parstagram

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import java.io.File

lateinit var homeBtn : ImageButton
lateinit var postBtn : ImageButton
lateinit var profileBtn : ImageButton
lateinit var postsRV : RecyclerView
lateinit var adapter : PostsAdapter

var posts = mutableListOf<Post>()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeBtn = findViewById(R.id.id_homeBtn)
        postBtn = findViewById(R.id.id_postBtn)
        profileBtn = findViewById(R.id.id_profileBtn)
        loadingProgress = findViewById(R.id.id_loadingProgress)

        homeBtn.isSelected = true
        profileBtn.isSelected = false

        postsRV = findViewById(R.id.id_postsRV)
        adapter = PostsAdapter(posts , this)

        postsRV.layoutManager = LinearLayoutManager(this)
        postsRV.adapter = adapter

        postBtn.setOnClickListener {
            // launch camera to let user take picture
            onLaunchCamera()
        }

        profileBtn.setOnClickListener {
            val intent = Intent(this , ProfilePage::class.java)
            startActivity(intent)
        }

        queryPosts()

    }

    // Query for all posts in our server
    fun queryPosts() {
        Log.d("RITIKA" , "mainactivity queryPosts")
        // Specify which class to query
        val query : ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all Post objects
        query.include(Post.KEY_USER)
        query.findInBackground(object : FindCallback<Post> {
            override fun done(postsList: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    Log.e("RITIKA" , "queryPosts failure ${e}")
                } else {
                    if (postsList != null) {
                        posts.clear()
                        posts.addAll(postsList.asReversed())
                        adapter.notifyDataSetChanged()
                        for (post in posts) {
                            //Log.i("RITIKA" , "user: ${post.getUser()?.username} ; image: ${post.getImage()} ; comment: ${post.getDescription()}")
                        }
                    }
                }
            }
        })
    }

    fun onLaunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFileUri(photoFileName)

        if (photoFile != null) {
            val fileProvider: Uri = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "RITIKA")

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("RITIKA", "failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator + fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("RITIKA" , "onActivityResult")
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val intent = Intent(this , CreatePost::class.java)
                intent.putExtra("TWEET_EXTRA" , photoFile!!.absolutePath)
                startActivityForResult(intent , SHARE_POST_SUCCESS_REQUEST_CODE)
            } else {
                Log.d("RITIKA" , "picture wasn't taken")
                val toast = Toast.makeText(MainActivity@this, "Was not able to take picture. Please try again later.", Toast.LENGTH_LONG);
                toast.view?.setBackgroundColor(Color.RED)
                toast.show()
            }
        }

        if (requestCode == SHARE_POST_SUCCESS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("RITIKA" , "sharebtn clicked")
            }
        }
    }

    companion object {
        val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
        val SHARE_POST_SUCCESS_REQUEST_CODE = 1234
        val photoFileName = "photo.jpg"
        var photoFile: File? = null
        var loadingProgress : ProgressBar? = null
    }

}