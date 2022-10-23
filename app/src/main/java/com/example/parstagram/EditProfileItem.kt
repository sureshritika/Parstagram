package com.example.parstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import com.parse.ParseUser

class EditProfileItem : AppCompatActivity() {

    lateinit var item : TextView
    lateinit var itemInfo : EditText
    lateinit var cancelBtn : Button
    lateinit var doneBtn : Button
    lateinit var doneProgress : ProgressBar

    var itemStr : String? = null
    var itemInfoStr : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_item)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.header_profile_item)
        val view = supportActionBar!!.customView
        (view.parent as Toolbar).setContentInsetsAbsolute(0 , 0)

        itemStr = intent.getStringExtra("item")
        itemInfoStr = intent.getStringExtra("itemInfo")
        Log.d("RITIKA" , "got from editprofile to editprofileitem item: ${itemStr} ; itemInfo: ${itemInfoStr}")

        item = findViewById(R.id.id_item)
        itemInfo = findViewById(R.id.id_itemInfo)
        cancelBtn = findViewById(R.id.id_cancelBtn)
        doneBtn = findViewById(R.id.id_doneBtn)
        doneProgress = findViewById(R.id.id_doneProgress)

        view.findViewById<TextView>(R.id.id_name).text = itemStr

        item.text = itemStr
        itemInfo.setText(itemInfoStr)

        cancelBtn.setOnClickListener {
            onBackPressed()
        }

        doneBtn.setOnClickListener {
            Log.d("RITIKA" , "turn loading prog on ${doneProgress}")
            progressOn()
            Log.d("RITIKA" , "put ${itemInfo.text} into ${item.text.toString().toLowerCase()} ")
            ParseUser.getCurrentUser().put(item.text.toString().toLowerCase(), itemInfo.text.toString())
            ParseUser.getCurrentUser().saveInBackground() { exception ->
                Log.d("RITIKA" , "turn loading prog off ${doneProgress}")
                progressOff()
                if (exception != null) {
                    // Something has went wrong
                    Log.e("RITIKA" , "doneBtn failure ${exception}")
                    exception.printStackTrace()
                } else {
                    Log.i("RITIKA" , "doneBtn success")
                    val intent = Intent()
                    intent.putExtra("itemInfo" , itemInfo.text.toString())
                    Log.d("RITIKA" , "sending from editprofileitem to editprofile ${itemInfo.text}")
                    setResult(RESULT_OK , intent)
                    finish()
                }
            }
        }

    }

    fun progressOn() {
        doneProgress.visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.id_editProfileLayout)?.children?.iterator()?.forEach {
            it.isEnabled = false
            it.alpha = 0.25F
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
        cancelBtn.isEnabled = true
        cancelBtn.alpha = 1F
        doneBtn.isEnabled = true
        doneBtn.alpha = 1F
    }

}