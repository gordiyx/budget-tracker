package com.alterpat.budgettracker

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

class AddTransactionActivity : AppCompatActivity() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        labelInput.addTextChangedListener {
            if (it!!.count() > 0)
                labelLayout.error = null
        }

        amountInput.addTextChangedListener {
            if (it!!.count() > 0)
                amountLayout.error = null
        }

        dateInput.setOnClickListener {
            showDatePickerDialog()
        }

        incomeBtn.setOnClickListener {
            handleTransaction(true)
        }

        expenseBtn.setOnClickListener {
            handleTransaction(false)
        }

        closeBtn.setOnClickListener {
            finish()
        }
    }

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

    private fun handleTransaction(isIncome: Boolean) {
        val label = labelInput.text.toString()
        val description = descriptionInput.text.toString()
        val amount = amountInput.text.toString().toDoubleOrNull()
        val dateString = dateInput.text.toString()

        if (label.isEmpty()) {
            labelLayout.error = "Please enter a valid label"
            return
        }

        if (amount == null) {
            amountLayout.error = "Please enter a valid amount"
            return
        }

        if (dateString.isEmpty()) {
            dateLayout.error = "Please select a date"
            return
        }

        val date: Date = try {
            dateFormat.parse(dateString)!!
        } catch (e: Exception) {
            dateLayout.error = "Invalid date format"
            return
        }

        val finalAmount = if (isIncome) amount else -amount
        val transaction = Transaction(0, label, finalAmount, date, description)
        insert(transaction)
    }

    private fun insert(transaction: Transaction) {
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }
}
