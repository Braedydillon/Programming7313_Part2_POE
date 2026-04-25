package com.example.tracker

import Data.database.AppDatabase
import android.app.DatePickerDialog
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
    private lateinit var btnFilter: Button
    private lateinit var txtTotal: TextView
        private lateinit var txtExpenses: TextView
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report)

        db = AppDatabase.getDatabase(this)

        edtStartDate = findViewById(R.id.startdate)
        edtEndDate = findViewById(R.id.enddate)
        btnFilter = findViewById(R.id.filter)
        txtTotal = findViewById(R.id.Total)
        txtExpenses = findViewById(R.id.expenses)

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
    }

    private fun filterExpenses() {
        val startDate = edtStartDate.text.toString()
        val endDate = edtEndDate.text.toString()

        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val filteredExpenses = db.expenseDao().getExpensesBetweenDates(startDate, endDate)
            
            runOnUiThread {
                if (filteredExpenses.isEmpty()) {
                    txtExpenses.text = "No expenses found between the selected dates"
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
}
