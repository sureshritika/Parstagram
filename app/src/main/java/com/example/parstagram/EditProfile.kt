package com.example.parstagram

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.iterator
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class EditProfile : AppCompatActivity() {

    lateinit var profileImg : ImageView
    lateinit var changeProfImgBtn : Button
    lateinit var profileInfoLayout : TableLayout
    lateinit var nameInfoTxt : TextView
    lateinit var usernameInfoTxt : TextView
    lateinit var bioInfoTxt : TextView
    lateinit var cancelBtn : Button
    lateinit var doneBtn : Button
    lateinit var doneProgress : ProgressBar

    var row : Int = 0
    var CHANGE_PROFILE_INFO_REQUEST_CODE = 1234
    var photoFileName = "profileImg.jpg"
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.header_edit_profile)
        val view = supportActionBar!!.customView
        (view.parent as Toolbar).setContentInsetsAbsolute(0 , 0)

        profileImg = findViewById(R.id.id_profileImg)
        changeProfImgBtn = findViewById(R.id.id_changeProfImgBtn)
        profileInfoLayout = findViewById(R.id.id_profileInfoLayout)
        nameInfoTxt = findViewById(R.id.id_nameInfoTxt)
        usernameInfoTxt = findViewById(R.id.id_usernameInfoTxt)
        bioInfoTxt = findViewById(R.id.id_bioInfoTxt)
        cancelBtn = findViewById(R.id.id_cancelBtn)
        doneBtn = findViewById(R.id.id_doneBtn)
        doneProgress = findViewById(R.id.id_doneProgress)

        Glide.with(this).load(ParseUser.getCurrentUser().getParseFile("profileImage")?.url).apply(RequestOptions.circleCropTransform()).into(profileImg)
        nameInfoTxt.text = ParseUser.getCurrentUser().getString("name")
        usernameInfoTxt.text = ParseUser.getCurrentUser().getString("username")
        bioInfoTxt.text = ParseUser.getCurrentUser().getString("bio")

        profileInfoLayout.iterator().forEach { it ->
            it.setOnClickListener {
                it as TableRow
                row = profileInfoLayout.indexOfChild(it)
                val item = ((it)[0] as TextView).text
                val itemInfo = ((it)[1] as TextView).text
                Log.d("RITIKA" , "sending item: ${item} ; itemInfo: ${itemInfo}")
                val intent = Intent(this , EditProfileItem::class.java)
                intent.putExtra("item" , "$item")
                intent.putExtra("itemInfo" , "$itemInfo")
                startActivityForResult(intent , CHANGE_PROFILE_INFO_REQUEST_CODE)
            }

        }

        cancelBtn.setOnClickListener {
            finish()
        }

        doneBtn.setOnClickListener {
            Log.d("RITIKA" , "turn loading prog on ${doneProgress}")
            progressOn()
            ParseUser.getCurrentUser().put("name" , nameInfoTxt.text)
            ParseUser.getCurrentUser().put("username" , usernameInfoTxt.text)
            ParseUser.getCurrentUser().put("bio" , bioInfoTxt.text)
            Log.d("RITIKA" , "photogile $photoFile")
            if (photoFile != null)
                ParseUser.getCurrentUser().put("profileImage" , ParseFile(photoFile))
            ParseUser.getCurrentUser().saveInBackground() { exception ->
                Log.d("RITIKA" , "turn loading prog off ${doneProgress}")
                progressOff()
                if (exception != null) {
                    // Something has went wrong
                    Log.e("RITIKA" , "doneBtn failure ${exception}")
                    exception.printStackTrace()
                } else {
                    Log.i("RITIKA" , "doneBtn success")
                    setResult(RESULT_OK , intent)
                    finish()
                }
            }
        }

        changeProfImgBtn.setOnClickListener {
            val popup = PopupMenu(this, changeProfImgBtn)
            popup.menuInflater.inflate(R.menu.change_prof_img_menu, popup.menu)
            popup.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    if (item.itemId == R.id.id_takePhotoBtn) {
                        Log.d("RITIKA" , "takephoto")
                        onLaunchCamera()
                    }
                    if (item.itemId == R.id.id_chooseFromGalleryBtn) {
                        Log.d("RITIKA" , "gallery")
                        onPickPhoto()
                    }
                    return true
                }
            })
            popup.show()
        }

    }

    fun progressOn() {
        doneProgress.visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.id_editProfileLayout)?.children?.iterator()?.forEach {
            it.isEnabled = false
            it.alpha = 0.25F
        }
        profileInfoLayout.children.forEach {
            (it as TableRow).isEnabled = false
        }
        cancelBtn.isEnabled = false
        cancelBtn.alpha = 0.25F
        doneBtn.isEnabled = false
        doneBtn.alpha = 0.25F
    }

    fun progressOff() {
        doneProgress.visibility = View.GONE
        findViewById<RelativeLayout>(R.id.id_editProfileLayout)?.children?.iterator()?.forEach {
            it.isEnabled = true
            it.alpha = 1F
        }
        profileInfoLayout.children.forEach {
            (it as TableRow).isEnabled = true
        }
        cancelBtn.isEnabled = true
        cancelBtn.alpha = 1F
        doneBtn.isEnabled = true
        doneBtn.alpha = 1F
    }

    fun onLaunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFileUri(photoFileName)
        Log.d("RITIKA" , "launch file $photoFile")
        if (photoFile != null) {
            val fileProvider: Uri = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (intent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }
    fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir =
            File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "RITIKA")

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("RITIKA", "failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator + fileName)
    }

    fun onPickPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, PICK_PHOTO_CODE)
        }
    }

    fun loadFromStream(inputStream : InputStream) {
        val storeDirectory = this.getExternalFilesDir(Environment.DIRECTORY_DCIM) // DCIM folder
        photoFile = File(storeDirectory, photoFileName)
        inputStream.use { input ->
            val outputStream = FileOutputStream(photoFile)
            outputStream.use { output -> val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == CHANGE_PROFILE_INFO_REQUEST_CODE) {
            val itemInfo = data?.getStringExtra("itemInfo")
            Log.d("RITIKA" , "got from editprofileitem to editprofile ${itemInfo}")
            ((profileInfoLayout[row] as TableRow)[1] as TextView).text = itemInfo
        }

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                Glide.with(this).load(takenImage).apply(RequestOptions.circleCropTransform()).into(profileImg)
            } else {
                Log.d("RITIKA" , "picture wasn't taken")
                val toast = Toast.makeText(this, "Was not able to take picture. Please try again later.", Toast.LENGTH_LONG);
                toast.view?.setBackgroundColor(Color.RED)
                toast.show()
            }
        }

        if (requestCode === PICK_PHOTO_CODE && data != null) {
            val inputStream: InputStream = this.getContentResolver().openInputStream(data.data!!)!!
            loadFromStream(inputStream)
            val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            Glide.with(this).load(takenImage).apply(RequestOptions.circleCropTransform()).into(profileImg)

        }

    }

    companion object {
        val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
        val PICK_PHOTO_CODE = 1046
    }
}