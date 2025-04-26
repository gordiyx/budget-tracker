package com.gordiyx.budgettracker

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


/**
 * Activity for adding a new transaction (income or expense).
 * Handles user input validation, date selection, and database insertion.
 */

class AddTransactionActivity : AppCompatActivity() {

    // Formatter for displaying and parsing dates
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Validate label input in real-time
        labelInput.addTextChangedListener {
            if (it!!.count() > 0)
                labelLayout.error = null
        }

        // Validate amount input in real-time
        amountInput.addTextChangedListener {
            if (it!!.count() > 0)
                amountLayout.error = null
        }

        // Open date picker dialog on date field click
        dateInput.setOnClickListener {
            showDatePickerDialog()
        }

        // Handle income button click
        incomeBtn.setOnClickListener {
            handleTransaction(true)
        }

        // Handle expense button click
        expenseBtn.setOnClickListener {
            handleTransaction(false)
        }

        // Handle close button click
        closeBtn.setOnClickListener {
            finish()
        }
    }


     /**
     * Displays a date picker dialog and sets the selected date into the date input field.
     */

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                dateInput.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }


     /**
     * Validates user input and creates a Transaction object for insertion.
     * @param isIncome Boolean indicating whether the transaction is income (true) or expense (false).
     */

    private fun handleTransaction(isIncome: Boolean) {
        val label = labelInput.text.toString()
        val description = descriptionInput.text.toString()
        val amount = amountInput.text.toString().toDoubleOrNull()
        val dateString = dateInput.text.toString()

        // Validate label field
        if (label.isEmpty()) {
            labelLayout.error = "Please enter a valid label"
            return
        }

        // Validate amount field
        if (amount == null) {
            amountLayout.error = "Please enter a valid amount"
            return
        }

        // Validate date field
        if (dateString.isEmpty()) {
            dateLayout.error = "Please select a date"
            return
        }

        // Parse the selected date
        val date: Date = try {
            dateFormat.parse(dateString)!!
        } catch (e: Exception) {
            dateLayout.error = "Invalid date format"
            return
        }

        // If expense, make the amount negative
        val finalAmount = if (isIncome) amount else -amount
        val transaction = Transaction(
            id = 0, 
            label = label, 
            amount = finalAmount, 
            date = date, 
            description = description
        )

        insert(transaction)
    }


     /**
     * Inserts the Transaction object into the database.
     * @param transaction Transaction object to insert.
     */
    pprivate fun insert(transaction: Transaction) {
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "transactions"
        ).build()

        // Insert transaction in a background coroutine
        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }
}
