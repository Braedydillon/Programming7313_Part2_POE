package com.example.tracker

import Data.Expense
import Data.MonthlyGoal
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import Data.database.AppDatabase
import android.app.DatePickerDialog
import android.icu.util.Calendar
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
import java.util.Locale

class Expenses : AppCompatActivity() {

    //global declarations
    private lateinit var edtCategory: EditText
    private lateinit var edtAmount: EditText
    private lateinit var edtDate: EditText
    private lateinit var edtDescription: EditText
    private lateinit var btnPhoto: Button
    private lateinit var btnSave: Button
    private lateinit var edtMinimumgoal: EditText
    private lateinit var edtMaximumgoal: EditText
    private lateinit var btnSave2: Button

    private lateinit var db: AppDatabase

    private var selectedPhotoUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expenses)

        //typecasting
        edtCategory = findViewById(R.id.edtCategory)
        edtAmount = findViewById(R.id.edtAmount)
        edtDate = findViewById(R.id.edtDate)
        edtDescription = findViewById(R.id.edtDescription)
        btnPhoto = findViewById(R.id.btnPhoto)
        btnSave = findViewById(R.id.btnSave)
        edtMinimumgoal = findViewById(R.id.edtMinimumgoal)
        edtMaximumgoal = findViewById(R.id.edtMaximumgoal)
        btnSave2 = findViewById(R.id.btnSave2)

        db = AppDatabase.getDatabase(this)

        edtDate.setOnClickListener {
            showDatePicker()
        }

        btnPhoto.setOnClickListener {
            imagePickerLauncher.launch(arrayOf("image/*"))
        }

        btnSave.setOnClickListener {
            saveExpense()
        }

        btnSave2.setOnClickListener {
            saveGoals()
        }




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                // Grant persistable permission so the app can access the image even after a restart
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            selectedPhotoUrl = uri.toString()
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveExpense(){
        val category = edtCategory.text.toString().trim()
        val amountText = edtAmount.text.toString().trim()
        val date = edtDate.text.toString().trim()
        val description = edtDescription.text.toString().trim()

        //validation checks
        if(category.isEmpty()|| amountText.isEmpty()|| date.isEmpty()||description.isEmpty()){
            Toast.makeText(this,"Please fill in all the required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()

        if(amount == null){
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val expense = Expense(
            category = category,
            amount = amount,
            date = date,
            description = description,
            photoUri = selectedPhotoUrl
        )

        lifecycleScope.launch {
            db.expenseDao().insertExpense(expense)

            runOnUiThread {
                Toast.makeText(this@Expenses, "Expense save successfully", Toast.LENGTH_SHORT).show()

                edtCategory.text.clear()
                edtAmount.text.clear()
                edtDate.text.clear()
                edtDescription.text.clear()
                selectedPhotoUrl = null

            }
        }
    }

    private fun saveGoals(){
        val minText = edtMinimumgoal.text.toString().trim()
        val maxText = edtMaximumgoal.text.toString().trim()

        if(minText.isEmpty()|| maxText.isEmpty()){
            Toast.makeText(this,"Please fill in all the required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val minGoal = minText.toDoubleOrNull()
        val maxGoal = maxText.toDoubleOrNull()

        if(minGoal == null || maxGoal == null){
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        if(minGoal > maxGoal){
            Toast.makeText(this, "Minimum goal cannot be greater than maximum goal", Toast.LENGTH_SHORT).show()
            return
        }


        lifecycleScope.launch {
            val existingGoal = db.monthlyDao().getGoal()


            if(existingGoal == null){
                val newGoal = MonthlyGoal(
                    minGoal = minGoal,
                    maxGoal = maxGoal
                )
                db.monthlyDao().insertGoal(newGoal)
            }else{
                val updatedGoal = existingGoal.copy(
                    minGoal = minGoal,
                    maxGoal = maxGoal
                )
                db.monthlyDao().updateGoal(updatedGoal)
            }
            runOnUiThread {
                Toast.makeText(this@Expenses, "Goal save successfully", Toast.LENGTH_SHORT).show()
                edtMinimumgoal.text.clear()
                edtMaximumgoal.text.clear()
            }
        }

        }
    private fun showDatePicker() {
val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = String.format(Locale.getDefault(), "%02d", selectedMonth + 1)
                val formattedDay = String.format(Locale.getDefault(), "%02d", selectedDay)

                val selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                edtDate.setText(selectedDate)
            },
            year,
            month,
            day
        )
    datePickerDialog.show()

}
    }

