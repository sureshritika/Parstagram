package com.example.parstagram.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.parstagram.EditProfile
import com.example.parstagram.LoginActivity
import com.example.parstagram.MainActivity
import com.example.parstagram.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.ParseException
import com.parse.ParseUser


open class ProfileFragment : Fragment {

    lateinit var profileImg : ImageView
    lateinit var postCount : TextView
    lateinit var postTxt : TextView
    lateinit var followerCount : TextView
    lateinit var followerTxt : TextView
    lateinit var followingCount : TextView
    lateinit var name : TextView
    lateinit var bio : TextView

    var user = ParseUser()
    var header = 0
    var buttons = 0
    lateinit var editProfileBtn : Button
    lateinit var logoutBtn : Button
    lateinit var logoutProgress : ProgressBar
    lateinit var followBtn : Button

    constructor() {
        user = ParseUser.getCurrentUser()
        header = R.layout.header_profile
        buttons = R.layout.buttons_profile

    }

    constructor(user: ParseUser) {
        this.user = user
        header = R.layout.header_other_profile
        if (user.objectId == ParseUser.getCurrentUser().objectId)
            buttons = R.layout.buttons_other_me
        else
            buttons = R.layout.buttons_other
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar?.setCustomView(header)
        val bar = (activity as MainActivity).supportActionBar!!.customView
        bar.findViewById<TextView>(R.id.id_username)?.text = user.username

        val child = layoutInflater.inflate(buttons, null) as LinearLayout
        child.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        view.findViewById<RelativeLayout>(R.id.id_buttonLayout).addView(child)

        profileImg = view.findViewById(R.id.id_profileImg)
        postCount = view.findViewById(R.id.id_postCount)
        postTxt = view.findViewById(R.id.id_postTxt)
        followerCount = view.findViewById(R.id.id_followerCount)
        followerTxt = view.findViewById(R.id.id_followerTxt)
        followingCount = view.findViewById(R.id.id_followingCount)
        name = view.findViewById(R.id.id_name)
        bio = view.findViewById(R.id.id_bio)

        if (buttons == R.layout.buttons_profile) {
            logoutBtn()
        }
        if (buttons == R.layout.buttons_profile || buttons == R.layout.buttons_other_me)
            editProfileBtn()
        else
            followBtn()

        childFragmentManager.beginTransaction().add(R.id.id_userContainer, ProfileRVFragment(user)).commit()

        updateProfile()

        postCount.text = user.getInt("posts").toString()
        if (Integer.parseInt(postCount.text as String) == 1)
            postTxt.text = "Post"
        else
            postTxt.text = "Posts"
        followerCount.text = user.getInt("followers").toString()
        if (Integer.parseInt(followerCount.text as String) == 1)
            followerTxt.text = "Follower"
        else
            followerTxt.text = "Followers"
        followingCount.text = user.getInt("following").toString()


        bar?.findViewById<ImageButton>(R.id.id_back)?.setOnClickListener {
            fragmentManager?.popBackStack()
        }

    }

    private fun progressVisible(progress : ProgressBar) {
        progress.visibility = View.VISIBLE
        editProfileBtn.isClickable = false
        editProfileBtn.alpha = 0.25F
        logoutBtn.isClickable = false
        logoutBtn.alpha = 0.25F
        requireActivity().findViewById<BottomNavigationView>(R.id.id_footerMenu).alpha = 0.25F
        requireActivity().findViewById<BottomNavigationView>(R.id.id_footerMenu).menu.forEach {
            it.isEnabled = false
        }


    }

    private fun progressInvisible(progress: ProgressBar) {
        progress.visibility = View.GONE
        editProfileBtn.isClickable = true
        editProfileBtn.alpha = 1F
        logoutBtn.isClickable = true
        logoutBtn.alpha = 1F
        requireActivity().findViewById<BottomNavigationView>(R.id.id_footerMenu).alpha = 1F
        requireActivity().findViewById<BottomNavigationView>(R.id.id_footerMenu).menu.forEach {
            it.isEnabled = true
        }
    }

    fun logoutBtn() {
        logoutBtn = requireActivity().findViewById(R.id.id_logoutBtn)
        logoutProgress = requireActivity().findViewById(R.id.id_logoutProgress)

        logoutBtn.setOnClickListener {
            Log.d("RITIKA" , "logoutUser clicked")
            val builder = AlertDialog.Builder(requireActivity() , R.style.AlertDialogTheme)
            builder.setTitle("Log out of Instagram?")
            builder.setPositiveButton("Log out") { dialog, which ->
                progressVisible(logoutProgress)
                ParseUser.logOutInBackground { e: ParseException? ->
                    progressInvisible(logoutProgress)
                    if (e == null) {
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            builder.setNeutralButton("Cancel") { dialog, which ->
            }
            builder.setCancelable(false)
            builder.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == PROFILE_UPDATE_REQUEST_CODE) {
            Log.d("RITIKA" , "here")
            updateProfile()
        } else {
            Log.d("RITIKA" , "profile update error")
        }
    }

    fun editProfileBtn() {
        editProfileBtn = requireActivity().findViewById(R.id.id_editProfileBtn)

        editProfileBtn.setOnClickListener {
            val intent = Intent(requireContext() , EditProfile::class.java)
            startActivityForResult(intent , PROFILE_UPDATE_REQUEST_CODE)
        }
    }

    fun followBtn() {
        followBtn = requireActivity().findViewById(R.id.id_followBtn)

        followBtn.setOnClickListener {
            Log.d("RITIKA" , "clicked follow")
        }
    }

    fun updateProfile() {
        Glide.with(requireContext()).load(user.getParseFile("profileImage")?.url).placeholder(R.drawable.loading).thumbnail(Glide.with(requireContext()).load(R.drawable.loading)).apply(RequestOptions.circleCropTransform()).into(profileImg)
        name.text = user.getString("name")
        bio.text = user.getString("bio")
    }

    companion object {
        var PROFILE_UPDATE_REQUEST_CODE = 2345
    }

}