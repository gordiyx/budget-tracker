package com.gordiyx.budgettracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_detailed.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


/**
 * Activity for displaying, editing, and deleting a single transaction.
 */

class DetailedActivity : AppCompatActivity() {

    private lateinit var transaction: Transaction
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var recentlyDeletedTransaction: Transaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        // Get the transaction passed from previous screen
        transaction = intent.getSerializableExtra("transaction") as Transaction

        // Fill input fields with transaction data
        labelInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        dateInput.setText(dateFormat.format(transaction.date))
        descriptionInput.setText(transaction.description)

        // Hide keyboard when tapping outside inputs
        rootView.setOnClickListener {
            this.window.decorView.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        // Listen for changes to show the update button
        labelInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if (it!!.isNotEmpty())
                labelLayout.error = null
        }

        amountInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if (it!!.isNotEmpty())
                amountLayout.error = null
        }

        descriptionInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
        }

        // Update button click listener
        updateBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val dateString = dateInput.text.toString()

            if (label.isEmpty())
                labelLayout.error = "Please enter the name of the"
            else if (amount == null)
                amountLayout.error = "Please enter the cost"
            else if (dateString.isEmpty())
                dateLayout.error = "Please select a date"
            else {
                val date: Date = try {
                    dateFormat.parse(dateString)!!
                } catch (e: Exception) {
                    dateLayout.error = "Incorrect date format"
                }

                val updatedTransaction = Transaction(transaction.id, label, amount, date, description)
                update(updatedTransaction)
            }
        }

        // Delete button click listener
        deleteBtn.setOnClickListener {
            deleteTransaction(transaction)
        }

        // Back button click listener
        backBtn.setOnClickListener {
            finish()
        }
    }


    /**
     * Updates the transaction in the database.
     */

    private fun update(transaction: Transaction) {
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            runOnUiThread {
                finish()    // Close the screen after update
            }
        }
    }


    /**
     * Deletes the transaction from the database and returns to main screen.
     */

    private fun deleteTransaction(transaction: Transaction) {
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()

        GlobalScope.launch {
            db.transactionDao().delete(transaction)
            recentlyDeletedTransaction = transaction
            runOnUiThread {
                // Pass deleted transaction back to main screen (optional undo functionality)
                val intent = Intent(this@DetailedActivity, MainActivity::class.java).apply {
                    putExtra("deletedTransaction", recentlyDeletedTransaction)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}
