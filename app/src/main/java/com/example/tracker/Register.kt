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
import Data.User
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {

    //global declarations
    //These variables are declared globally so we can use them in multiple functions

    private lateinit var editTextText2: EditText
    private lateinit var editTextTextPassword2: EditText
    private lateinit var editTextTextPassword3: EditText
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        //typecasting (connecting the document to kotlin)
        editTextText2 = findViewById(R.id.editTextText2)
        editTextTextPassword2 = findViewById(R.id.editTextTextPassword2)
        editTextTextPassword3 = findViewById(R.id.editTextTextPassword3)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)

        //initialize the database
        db = AppDatabase.getDatabase(this)//initializing the database

        addDefaultUser()

        //button click event
        //When the user clickers the register button, it will activate the code within

        button2.setOnClickListener {
            registerUser() //calling function to handle register
        }

        //Button for when users already have an account
        button3.setOnClickListener {
            openLoginScreen() //user will be directed to the login screen
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //function to handle the logic when a user registers
    private fun registerUser(){

        //get text from input fields and remove extra spaces
        val username = editTextText2.text.toString().trim()
        val password = editTextTextPassword2.text.toString().trim()
        val confirmPassword = editTextTextPassword3.text.toString().trim()

        //Validation
        //check if fields are empty

        if(username.isEmpty()|| password.isEmpty()|| confirmPassword.isEmpty()){
            Toast.makeText(this,"Please fill in all the fields", Toast.LENGTH_SHORT).show()
            return // stops the function if validation fails
        }

        //check if the passwords match
        if(password != confirmPassword){
            Toast.makeText(this,"Password do not match", Toast.LENGTH_SHORT).show()
            return
        }

        //Database operation
        //lifecycle.Scope.launch runs code in the background

        lifecycleScope.launch {
            val existingUser = db.userDao().getUserByUsername(username)

            if(existingUser != null){
                //if the user exist,show message on sceen
                runOnUiThread {
                    Toast.makeText(this@Register,"Username already exists", Toast.LENGTH_SHORT).show()
                }
            } else {
                //if the user does not exist, create a new user object
                val newUser = User(
                    username = username,
                    password = password
                )

                //Insert new user into the database
                db.userDao().insertUser(newUser)

                //show success message and move to the login screen
                runOnUiThread {
                    Toast.makeText(this@Register,"Registration successful", Toast.LENGTH_SHORT).show()

                    clearFields()
                    openLoginScreen()
                }
            }
        }


    }
    //This function ensures that there is always an admin user
    private fun addDefaultUser(){
        lifecycleScope.launch {
            //check if the admin already exists
            val existingUser = db.userDao().getUserByUsername("admin")
            if(existingUser == null){
                //if the admin does not exist, insert default
                db.userDao().insertUser(
                    User(username = "admin", password = "1234")
                )
            }

        }

    }

    private fun openLoginScreen(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() //Closes the current screen so user can not go back
    }

    private fun clearFields(){
        editTextText2.text.clear()
        editTextTextPassword2.text.clear()
        editTextTextPassword3.text.clear()
    }

}
