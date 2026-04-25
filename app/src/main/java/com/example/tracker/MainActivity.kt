package com.example.tracker

import Data.database.AppDatabase
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    //global declarations
    private lateinit var editTextText: EditText
    private lateinit var editTextTextPassword: EditText
    private lateinit var button: Button
    private lateinit var textView: Button
    private lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)

        editTextText = findViewById(R.id.editTextText)
        editTextTextPassword = findViewById(R.id.editTextTextPassword)
        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)


        button.setOnClickListener {
            loginUser()
        }

        textView.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loginUser() {
        val username = editTextText.text.toString().trim()
        val password = editTextTextPassword.text.toString().trim()


        //Validation checks
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT)
                .show()
            return
        }

        lifecycleScope.launch {
            //check if user exists with matching username and password
            val foundUser = db.userDao().loginUser(username, password)

            runOnUiThread {
                if (foundUser != null) {
                    //if the user is found -> login successful
                    Toast.makeText(this@MainActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                    //Go to the home page once successful
                    openHomePage(foundUser.username)

                } else {
                    //if user not found then login failed
                    Toast.makeText(
                        this@MainActivity, "Invalid username or password. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openHomePage(username: String) {
        val intent = Intent(this, Home::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
        finish()
    }

}