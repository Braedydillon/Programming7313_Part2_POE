package com.example.tracker

import Data.database.AppDatabase
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class Report : AppCompatActivity() {
    private lateinit var edtStartDate: EditText
    private lateinit var edtEndDate: EditText
    private lateinit var edtSearchName: EditText
    private lateinit var btnFilter: Button
    private lateinit var txtTotal: TextView
    private lateinit var txtExpenses: TextView
    private lateinit var db: AppDatabase

    private lateinit var returnhome: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report)

        db = AppDatabase.getDatabase(this)

        edtStartDate = findViewById(R.id.startdate)
        edtEndDate = findViewById(R.id.enddate)
        edtSearchName = findViewById(R.id.searchname)
        btnFilter = findViewById(R.id.filter)
        txtTotal = findViewById(R.id.Total)
        txtExpenses = findViewById(R.id.expenses)
        returnhome = findViewById(R.id.Return_From_Expenses)

        edtStartDate.setOnClickListener {
            showStartDatePicker()
        }
        edtEndDate.setOnClickListener {
            showEndDatePicker()
        }
        btnFilter.setOnClickListener {
            filterExpenses()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //home
        returnhome.setOnClickListener {
            Toast.makeText(this, "Going home", Toast.LENGTH_SHORT).show()

            // Point the intent specifically to your Home class
            val intent = Intent(this, Home::class.java)

            // Clear out any other screens sitting on top of the Home page
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)
            finish()
        }
    }

    private fun filterExpenses() {
        val startDate = edtStartDate.text.toString()
        val endDate = edtEndDate.text.toString()
        val searchName = edtSearchName.text.toString().trim()

        // 1. Check if everything is empty
        if (startDate.isEmpty() && endDate.isEmpty() && searchName.isEmpty()) {
            Toast.makeText(this, "Please enter dates or a search term", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Check if only one date is filled (preventing errors in the database query)
        if ((startDate.isNotEmpty() && endDate.isEmpty()) || (startDate.isEmpty() && endDate.isNotEmpty())) {
            Toast.makeText(this, "Please select both start and end dates, or clear them to search only by name", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            // 3. Fetch from database based on whether dates were provided
            var filteredExpenses = if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                db.expenseDao().getExpensesBetweenDates(startDate, endDate)
            } else {
                db.expenseDao().getAllExpenses() // Fallback to all expenses if dates are empty
            }

            // 4. If a search term was typed, filter the list down
            if (searchName.isNotEmpty()) {
                filteredExpenses = filteredExpenses.filter { expense ->
                    expense.category.contains(searchName, ignoreCase = true) ||
                            expense.description.contains(searchName, ignoreCase = true)
                }
            }

            // 5. Update the UI with the final list
            runOnUiThread {
                if (filteredExpenses.isEmpty()) {
                    txtExpenses.text = "No expenses found matching your criteria"
                    txtTotal.text = "Total: R0.00"
                } else {
                    var resultsText = ""
                    var totalAmount = 0.0

                    for (expense in filteredExpenses) {
                        resultsText += "Category: ${expense.category}\n"
                        resultsText += "Amount: R${expense.amount}\n"
                        resultsText += "Date: ${expense.date}\n"
                        resultsText += "Description: ${expense.description}\n\n"

                        totalAmount += expense.amount
                    }

                    txtExpenses.text = resultsText
                    txtTotal.text = "Total: R%.2f".format(totalAmount)
                }
            }
        }
    }

    private fun showStartDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)
                val selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                edtStartDate.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showEndDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)
                val selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                edtEndDate.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

 // return to home

}