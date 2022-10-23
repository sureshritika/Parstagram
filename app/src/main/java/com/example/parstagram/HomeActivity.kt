package com.example.parstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.parse.ParseUser

class HomeActivity : AppCompatActivity() {
    lateinit var loginBtn : Button
    lateinit var signupBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        loginBtn = findViewById(R.id.id_loginBtn)
        signupBtn = findViewById(R.id.id_signupBtn)

        if (ParseUser.getCurrentUser() != null) {
            goToMainActivity()
        }

        loginBtn.setOnClickListener {
            goToLoginActivity()
        }

        signupBtn.setOnClickListener {
            goToSignUpActivity()
        }

    }

    private fun goToMainActivity() {
        val intent = Intent(this@HomeActivity , MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goToSignUpActivity() {
        val intent = Intent(this@HomeActivity , SignupActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goToLoginActivity() {
        val intent = Intent(this@HomeActivity , LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}