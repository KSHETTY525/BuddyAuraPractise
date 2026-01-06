package com.example.buddyaura.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buddyaura.constants.Constants
import com.example.buddyaura.ui.login.MainActivity
import com.example.buddyaura.R

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.pass1)
        val email = findViewById<EditText>(R.id.email)
        val signupBtn = findViewById<Button>(R.id.signup)
        val signupText = findViewById<TextView>(R.id.signuptxt)

        // SharedPreferences
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(Constants.Companion.PREF_NAME, MODE_PRIVATE)

        signupBtn.setOnClickListener {

            val userText = username.text.toString().trim()
            val passText = password.text.toString().trim()
            val emailText = email.text.toString().trim()

            // ===== VALIDATIONS =====
            if (userText.isEmpty() || passText.isEmpty() || emailText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Regex("^[a-zA-Z]+$").matches(userText)) {
                Toast.makeText(this, "Username must contain only letters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Regex("^[a-zA-Z0-9]{8,12}$").matches(passText)) {
                Toast.makeText(this, "Password must be 8-12 letters & numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches() ||
                !emailText.endsWith("@gmail.com")
            ) {
                Toast.makeText(this, "Enter a valid Gmail address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ===== SAVE DATA =====
            val editor = sharedPreferences.edit()
            editor.putString(Constants.Companion.KEY_USERNAME, userText)
            editor.putString(Constants.Companion.KEY_PASSWORD, passText)
            editor.putString(Constants.Companion.KEY_EMAIL, emailText)
            editor.apply()

            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        signupText.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}