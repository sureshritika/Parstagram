package com.example.parstagram

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.parse.ParseUser
import org.w3c.dom.Text
import kotlin.math.log

class LoginActivity : AppCompatActivity() {

    lateinit var usernameEdit : TextInputEditText
    lateinit var passwordEdit : TextInputEditText
    lateinit var loginBtn : Button
    lateinit var loginProgress : ProgressBar
    lateinit var signupBtn : Button
    lateinit var signupProgress : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEdit = findViewById(R.id.id_usernameEdit)
        passwordEdit = findViewById(R.id.id_passwordEdit)
        loginBtn = findViewById(R.id.id_loginBtn)
        loginProgress = findViewById(R.id.id_loginProgress)
        signupBtn = findViewById(R.id.id_signupBtn)
        signupProgress = findViewById(R.id.id_signupProgress)

        buttonOff()

        passwordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                if (p0.isEmpty())
                    buttonOff()
                else
                    buttonOn()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        // Check if there's a user logged in
        // If there is, take them to MainActivity
        if (ParseUser.getCurrentUser() != null) {
            goToMainActivity()
        }

        loginBtn.setOnClickListener {
            val username = usernameEdit.text.toString()
            val password = passwordEdit.text.toString()
            loginUser(username , password)
        }

        signupBtn.setOnClickListener {
            val username = usernameEdit.text.toString()
            val password = passwordEdit.text.toString()
            signupUser(username , password)
        }

    }

    private fun signupUser(username : String , password : String) {
        Log.d("RITIKA" , "signUp clicked")
        progressVisible(signupProgress)

        // Create the ParseUser
        val user = ParseUser()

        // Set fields for the user to be created
        user.setUsername(username)
        user.setPassword(password)

        user.signUpInBackground { e ->
            progressInvisible(signupProgress)
            if (e == null) {
                // User has successfully created a new account
                Log.d("RITIKA" , "signUpUser success")
                goToMainActivity()
            } else {
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
                Log.d("RITIKA" , "signUpUser failure ${e}")
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun loginUser(username : String , password : String) {
        Log.d("RITIKA" , "loginUser clicked")
        progressVisible(loginProgress)
        ParseUser.logInInBackground(username, password, ({ user, e ->
            progressInvisible(loginProgress)
            if (user != null) {
                Log.i("RITIKA" , "loginUser success")
                goToMainActivity()
            } else {
                Log.i("RITIKA" , "loginUser failure ${e}")
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show();
            }})
        )
    }

    private fun goToMainActivity() {
        val intent = Intent(this@LoginActivity , MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun progressVisible(progress : ProgressBar) {
        progress.visibility = View.VISIBLE
        usernameEdit.isEnabled = false
        passwordEdit.isEnabled = false
        buttonOff()
    }

    private fun progressInvisible(progress : ProgressBar) {
        progress.visibility = View.INVISIBLE
        usernameEdit.isEnabled = true
        passwordEdit.isEnabled = true
        buttonOn()
    }

    private fun buttonOff() {
        loginBtn.alpha = 0.25F
        loginBtn.isClickable = false
        signupBtn.alpha = 0.25F
        signupBtn.isClickable = false
    }

    private fun buttonOn() {
        loginBtn.alpha = 1F
        loginBtn.isClickable = true
        signupBtn.alpha = 1F
        signupBtn.isClickable = true
    }

    companion object {

    }
}