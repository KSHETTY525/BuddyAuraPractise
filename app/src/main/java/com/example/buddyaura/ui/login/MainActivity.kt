package com.example.buddyaura.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buddyaura.constants.Constants
import com.example.buddyaura.ui.activity.HomeActivity
import com.example.buddyaura.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 SESSION CHECK (before setContentView)
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(Constants.Companion.PREF_NAME, MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean(Constants.Companion.KEY_IS_LOGGED_IN, false)

        if (isLoggedIn) {
            // User already logged in → go directly to Homepage
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Load login UI only if NOT logged in
        setContentView(R.layout.activity_main)

        val username = findViewById<EditText>(R.id.user)
        val password = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.btn1)
        val signupText = findViewById<TextView>(R.id.signup)

        loginBtn.setOnClickListener {

            val userText = username.text.toString().trim()
            val passText = password.text.toString().trim()

            val savedUser = sharedPreferences.getString(Constants.Companion.KEY_USERNAME, null)
            val savedPass = sharedPreferences.getString(Constants.Companion.KEY_PASSWORD, null)

            if (userText == savedUser && passText == savedPass) {

                // ✅ SAVE LOGIN SESSION
                sharedPreferences.edit()
                    .putBoolean(Constants.Companion.KEY_IS_LOGGED_IN, true)
                    .apply()

                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("username", savedUser)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(
                    this,
                    "Invalid username or password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        signupText.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            username.setText("")
            password.setText("")
            startActivity(intent)
        }
    }
}