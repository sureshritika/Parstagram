package com.example.parstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.parse.ParseUser

class SignupActivity : AppCompatActivity() {
    lateinit var nameEdit : TextInputEditText
    lateinit var usernameEdit : TextInputEditText
    lateinit var passwordEdit : TextInputEditText
    lateinit var signupBtn : Button
    lateinit var signupProgress : ProgressBar
    lateinit var loginBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        nameEdit = findViewById(R.id.id_nameEdit)
        usernameEdit = findViewById(R.id.id_usernameEdit)
        passwordEdit = findViewById(R.id.id_passwordEdit)
        signupBtn = findViewById(R.id.id_signupBtn)
        signupProgress = findViewById(R.id.id_signupProgress)
        loginBtn = findViewById(R.id.id_loginBtn)

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

        loginBtn.setOnClickListener {
            goToLoginActivity()
        }

        signupBtn.setOnClickListener {
            val name = nameEdit.text.toString()
            val username = usernameEdit.text.toString()
            val password = passwordEdit.text.toString()
            signupUser(name , username , password)
        }

    }

    private fun signupUser(name : String , username : String , password : String) {
        Log.d("RITIKA" , "signUp clicked")
        progressVisible()

        // Create the ParseUser
        val user = ParseUser()

        // Set fields for the user to be created
        user.put("name" , name)
        user.setUsername(username)
        user.setPassword(password)

        user.signUpInBackground { e ->
            progressInvisible()
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

    private fun goToMainActivity() {
        val intent = Intent(this@SignupActivity , MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goToLoginActivity() {
        val intent = Intent(this@SignupActivity , LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun progressVisible() {
        signupProgress.visibility = View.VISIBLE
        nameEdit.isEnabled = false
        usernameEdit.isEnabled = false
        passwordEdit.isEnabled = false
        loginBtn.alpha = 0.25F
        loginBtn.isEnabled = false
        buttonOff()
    }

    private fun progressInvisible() {
        signupProgress.visibility = View.INVISIBLE
        nameEdit.isEnabled = true
        usernameEdit.isEnabled = true
        passwordEdit.isEnabled = true
        loginBtn.alpha = 1F
        loginBtn.isEnabled = true
        buttonOn()
    }

    private fun buttonOff() {
        signupBtn.alpha = 0.25F
        signupBtn.isEnabled = false
    }

    private fun buttonOn() {
        signupBtn.alpha = 1F
        signupBtn.isEnabled = true
    }
}