package com.example.tracker

import Data.Expense
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.jvm.java

class Home : AppCompatActivity() {

    //global declarations
    private lateinit var btnExpenses: Button
    private lateinit var btnReports: Button
    private lateinit var btnLogout: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        //Typecasting
        btnExpenses = findViewById(R.id.btnExpenses)
        btnReports = findViewById(R.id.btnReports)
        btnLogout = findViewById(R.id.btnLogout)

        btnExpenses.setOnClickListener {
            Toast.makeText(this,"Open the expenses screen", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Expenses::class.java)
            startActivity(intent)
        }

        btnReports.setOnClickListener {
            Toast.makeText(this,"Open the reports screen", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Report::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            Toast.makeText(this,"Loging you out", Toast.LENGTH_SHORT).show()
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}