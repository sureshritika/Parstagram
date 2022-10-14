package com.example.parstagram

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.parse.*
import java.io.File

class CreatePost : AppCompatActivity() {

    var imgPath : String = ""
    var photoFile: File? = null

    lateinit var closeBtn : ImageButton
    lateinit var shareBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_post)

        imgPath = intent.getStringExtra("TWEET_EXTRA").toString()
        Log.d("RITIKA" , "takenImage = ${imgPath}")

        closeBtn = findViewById(R.id.id_closeBtn)
        shareBtn = findViewById(R.id.id_shareBtn)

        val takenImage = BitmapFactory.decodeFile(imgPath)
        val ivPreview : ImageView = findViewById(R.id.id_image)
        ivPreview.setImageBitmap(takenImage)

        shareBtn.setOnClickListener {
            // send post to server without image
            // get description that they have inputted
            Log.d("RITIKA" , "shareBtn clicked")
            val description = findViewById<EditText>(R.id.id_description).text.toString()
            if (description.isEmpty()) {
                Toast.makeText(this, "Caption is required.", Toast.LENGTH_LONG).show();
            }
            else {
                val user = ParseUser.getCurrentUser()
                photoFile = MainActivity.photoFile
                if (photoFile != null) {
                    Log.d("RITIKA", "shareBtn success")
                    submitPost(description, user, photoFile!!)
                    setResult(RESULT_OK)
                    Log.d("RITIKA" , "turn loading prog on ${MainActivity.loadingProgress}")
                    MainActivity.loadingProgress!!.visibility = View.VISIBLE
                    finish()
                } else {
                    Log.d("RITIKA", "shareBtn failure")
                    // TODO : Print error log message
                    // TODO : show a toast to user to let them know to take a picture
                }
            }
        }

        closeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    fun submitPost(description : String , user : ParseUser , file : File) {
        // Create the Post object
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground { exception ->
            Log.d("RITIKA" , "turn loading prog off ${MainActivity.loadingProgress}")
            MainActivity.loadingProgress!!.visibility = View.GONE
            if (exception != null) {
                 // Something has went wrong
                Log.e("RITIKA" , "submitPost failure ${exception}")
                val toast = Toast.makeText(MainActivity@this, "Was not able to share post. Please try again later.", Toast.LENGTH_LONG);
                toast.view?.setBackgroundColor(Color.RED)
                toast.show()
                exception.printStackTrace()
            } else {
                Log.i("RITIKA" , "submitPost success")
                Toast.makeText(MainActivity@this, "Successfully posted.", Toast.LENGTH_LONG).show()
                MainActivity().queryPosts()
            }
        }
    }

}