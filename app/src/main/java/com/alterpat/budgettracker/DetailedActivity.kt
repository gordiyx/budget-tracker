package com.alterpat.budgettracker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_detailed.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DetailedActivity : AppCompatActivity() {
    private lateinit var transaction: Transaction
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var recentlyDeletedTransaction: Transaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        transaction = intent.getSerializableExtra("transaction") as Transaction

        labelInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        dateInput.setText(dateFormat.format(transaction.date))
        descriptionInput.setText(transaction.description)

        rootView.setOnClickListener {
            this.window.decorView.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

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

        updateBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val dateString = dateInput.text.toString()

            if (label.isEmpty())
                labelLayout.error = "Будь ласка, введіть назву"
            else if (amount == null)
                amountLayout.error = "Будь ласка, введіть вартість"
            else if (dateString.isEmpty())
                dateLayout.error = "Будь ласка, оберіть дату"
            else {
                val date: Date = try {
                    dateFormat.parse(dateString)!!
                } catch (e: Exception) {
                    dateLayout.error = "Неправильний формат дати"
                    return@setOnClickListener
                }

                val updatedTransaction = Transaction(transaction.id, label, amount, date, description)
                update(updatedTransaction)
            }
        }

        deleteBtn.setOnClickListener {
            deleteTransaction(transaction)
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun update(transaction: Transaction) {
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            runOnUiThread {
                finish()
            }
        }
    }

    private fun deleteTransaction(transaction: Transaction) {
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()

        GlobalScope.launch {
            db.transactionDao().delete(transaction)
            recentlyDeletedTransaction = transaction
            runOnUiThread {
                val intent = Intent(this@DetailedActivity, MainActivity::class.java).apply {
                    putExtra("deletedTransaction", recentlyDeletedTransaction)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}
