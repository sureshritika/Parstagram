package com.example.parstagram.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.core.view.children
import com.example.parstagram.EditProfile
import com.example.parstagram.MainActivity
import com.example.parstagram.Post
import com.example.parstagram.R
import com.google.android.material.button.MaterialButton
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PostFragment : Fragment() {

    lateinit var takePictureBtn : Button
    lateinit var getPictureBtn : Button
    lateinit var ivPreview : ImageView
    lateinit var description : EditText
    lateinit var shareBtn : Button
    lateinit var shareProgress : ProgressBar

    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Set onClickListeners and setup logic

        (activity as MainActivity).supportActionBar?.setCustomView(R.layout.header_new_post)

        takePictureBtn = view.findViewById(R.id.id_takePictureBtn)
        getPictureBtn = view.findViewById(R.id.id_getPictureBtn)
        ivPreview = view.findViewById(R.id.id_image)
        description = view.findViewById(R.id.id_description)

        shareBtn = requireActivity().findViewById(R.id.id_sharePostBtn)
        Log.d("RITIKA" , "shareBTN ${shareBtn}")
        shareProgress = requireActivity().findViewById(R.id.id_shareProgress)

        takePictureBtn.setOnClickListener {
            onLaunchCamera()
        }

        getPictureBtn.setOnClickListener {
            onPickPhoto()
        }

        shareBtn.setOnClickListener {
            Log.d("RITIKA" , "shareBtn clicked")
            val descriptionStr = description.text.toString()
            if (descriptionStr.isEmpty() && ivPreview.drawable == null)
                Toast.makeText(requireContext(), "Caption is required. Image is required", Toast.LENGTH_LONG).show();
            else if (descriptionStr.isEmpty())
                Toast.makeText(requireContext(), "Caption is required.", Toast.LENGTH_LONG).show();
            else if (ivPreview.drawable == null)
                Toast.makeText(requireContext(), "Image is required", Toast.LENGTH_LONG).show();
            else {
                val user = ParseUser.getCurrentUser()
                if (photoFile != null) {
                    Log.d("RITIKA", "shareBtn success")
                    submitPost(descriptionStr, user, photoFile!!)
                    Log.d("RITIKA" , "turn loading prog on ${shareProgress}")
                    progressOn()
                } else {
                    Log.d("RITIKA", "shareBtn failure")
                }
            }
        }

    }

    fun onLaunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFileUri(photoFileName)

        if (photoFile != null) {
            val fileProvider: Uri = FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivityForResult(intent, EditProfile.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "RITIKA")

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("RITIKA", "failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator + fileName)
    }

    fun onPickPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(intent, EditProfile.PICK_PHOTO_CODE)
        }
    }

    fun loadFromStream(inputStream : InputStream) {
        val storeDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM) // DCIM folder
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
        Log.d("RITIKA" , "onActivityResult")
        if (requestCode == EditProfile.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                ivPreview.setImageBitmap(takenImage)
            } else {
                Log.d("RITIKA" , "picture wasn't taken")
                val toast = Toast.makeText(requireContext(), "Was not able to take picture. Please try again later.", Toast.LENGTH_LONG);
                toast.view?.setBackgroundColor(Color.RED)
                toast.show()
            }
        }

        if (requestCode === EditProfile.PICK_PHOTO_CODE && data != null) {
            val inputStream: InputStream = requireContext().getContentResolver().openInputStream(data.data!!)!!
            loadFromStream(inputStream)
            val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            ivPreview.setImageBitmap(takenImage)

        }
    }

    fun submitPost(descriptionStr : String , user : ParseUser , file : File) {
        // Create the Post object
        val post = Post()
        post.setDescription(descriptionStr)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground { exception ->
            Log.d("RITIKA" , "turn loading prog off ${shareProgress}")
            progressOff()
            if (exception != null) {
                Log.e("RITIKA" , "submitPost failure ${exception}")
                val toast = Toast.makeText(requireContext() , "Was not able to share post. Please try again later.", Toast.LENGTH_LONG);
                toast.view?.setBackgroundColor(Color.RED)
                toast.show()
                exception.printStackTrace()
            } else {
                Log.i("RITIKA" , "submitPost success")
                Toast.makeText(requireContext() , "Successfully posted.", Toast.LENGTH_LONG).show()
                description.setText("")
                ivPreview.setImageDrawable(null)
            }
        }
    }

    fun progressOn() {
        shareProgress!!.visibility = View.VISIBLE
        view?.findViewById<RelativeLayout>(R.id.id_layout)?.children?.iterator()?.forEach {
            it.isEnabled = false
            it.alpha = 0.25F
        }
        shareBtn.isEnabled = false
        shareBtn.alpha = 0.25F
    }

    fun progressOff() {
        shareProgress!!.visibility = View.GONE
        view?.findViewById<RelativeLayout>(R.id.id_layout)?.children?.iterator()?.forEach {
            it.isEnabled = true
            it.alpha = 1F
        }
        shareBtn.isEnabled = true
        shareBtn.alpha = 1F
    }

}