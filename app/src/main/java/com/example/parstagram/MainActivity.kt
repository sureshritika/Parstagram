package com.example.parstagram

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.parstagram.fragments.FeedFragment
import com.example.parstagram.fragments.PostFragment
import com.example.parstagram.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


lateinit var footerMenu : BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.header_ig)
        val view = supportActionBar!!.customView
        (view.parent as Toolbar).setContentInsetsAbsolute(0 , 0)

        footerMenu = findViewById(R.id.id_footerMenu)

        val fragmentManager : FragmentManager = supportFragmentManager

        footerMenu.itemIconTintList = null

        footerMenu.setOnItemSelectedListener {
            item ->
            var fragmentToShow : Fragment? = null
            when (item.itemId) {
                R.id.id_homeMenu -> {
                    fragmentToShow = FeedFragment()
                }
                R.id.id_postMenu -> {
                    fragmentToShow = PostFragment()
                }
                R.id.id_profileMenu -> {
                    fragmentToShow = ProfileFragment()
                }
            }

            if (fragmentToShow != null) {
                fragmentManager.beginTransaction().replace(R.id.id_flContainer, fragmentToShow).commit();
            }

            true
        }

        footerMenu.selectedItemId = R.id.id_homeMenu

    }

    companion object {
        var count = 20
    }



}