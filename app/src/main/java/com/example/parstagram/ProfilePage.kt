package com.example.parstagram

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.children
import com.parse.ParseException
import com.parse.ParseUser
import java.io.File


class ProfilePage : AppCompatActivity() {

    lateinit var homeBtn : ImageButton
    lateinit var postBtn : ImageButton
    lateinit var profileBtn : ImageButton
    lateinit var logoutBtn : Button
    lateinit var header : RelativeLayout
    lateinit var footer : RelativeLayout
    lateinit var logoutProgress : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_profile)

        homeBtn = findViewById(R.id.id_homeBtn)
        postBtn = findViewById(R.id.id_postBtn)
        profileBtn = findViewById(R.id.id_profileBtn)
        logoutBtn = findViewById(R.id.id_logoutBtn)
        logoutProgress = findViewById(R.id.id_logoutProgress)
        header = findViewById(R.id.id_header)
        footer = findViewById(R.id.id_footer)

        homeBtn.isSelected = false
        profileBtn.isSelected = true

        logoutBtn.setOnClickListener {
            Log.d("RITIKA" , "logoutUser clicked")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Log out of Instagram?")
            builder.setPositiveButton("Log out") { dialog, which ->
                progressVisible(logoutProgress)
                ParseUser.logOutInBackground { e: ParseException? ->
                    progressInvisible(logoutProgress)
                    if (e == null) {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            builder.setNeutralButton("Cancel") { dialog, which ->
            }
            builder.setCancelable(false)
            builder.show()
        }

        homeBtn.setOnClickListener {
            val intent = Intent(this , MainActivity::class.java)
            startActivity(intent)
        }

        postBtn.setOnClickListener {
            // launch camera to let user take picture
            onLaunchCamera()
        }

    }

    private fun progressVisible(progress : ProgressBar) {
        progress.visibility = View.VISIBLE
        header.children.iterator().forEach {
            it.isClickable = false
        }
        footer.children.iterator().forEach {
            it.isClickable = false
        }
        logoutBtn.alpha = 0.25F
        logoutBtn.isClickable = false

    }

    private fun progressInvisible(progress: ProgressBar) {
        progress.visibility = View.GONE
        header.children.iterator().forEach {
            it.isClickable = true
        }
        footer.children.iterator().forEach {
            it.isClickable = true
        }
        logoutBtn.alpha = 1F
        logoutBtn.isClickable = true
    }

    fun onLaunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        MainActivity.photoFile = getPhotoFileUri(MainActivity.photoFileName)

        if (MainActivity.photoFile != null) {
            val fileProvider: Uri = FileProvider.getUriForFile(this, "com.codepath.fileprovider", MainActivity.photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, MainActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "RITIKA")

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("RITIKA", "logout failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator + fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("RITIKA" , "logout onActivityResult")
        if (requestCode == MainActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val intent = Intent(this , CreatePost::class.java)
                intent.putExtra("TWEET_EXTRA" , MainActivity.photoFile!!.absolutePath)
                startActivityForResult(intent , MainActivity.SHARE_POST_SUCCESS_REQUEST_CODE)
            } else {
                Log.d("RITIKA" , "logout picture wasn't taken")
                // TODO: Toast message
            }
        }

        if (requestCode == MainActivity.SHARE_POST_SUCCESS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("RITIKA" , "logout sharebtn clicked")
                val intent = Intent(this , MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

}