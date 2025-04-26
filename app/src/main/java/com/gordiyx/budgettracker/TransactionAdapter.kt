package com.gordiyx.budgettracker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


/**
 * RecyclerView Adapter for displaying a list of transactions grouped by date.
 *
 * This adapter supports two view types:
 * - Date headers (showing the day of transactions)
 * - Individual transaction items (showing amount, label, and date)
 *
 * Each transaction item is clickable and opens a detailed view.
 *
 * @property transactions A prepared list containing both Date headers and Transaction objects.
 */

class TransactionAdapter(private var transactions: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    companion object {
        const val TYPE_DATE = 0
        const val TYPE_TRANSACTION = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (transactions[position] is Date) TYPE_DATE else TYPE_TRANSACTION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_DATE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false)
            DateViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
            TransactionViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DateViewHolder) {
            val date = transactions[position] as Date
            holder.dateText.text = dateFormat.format(date)
        } else if (holder is TransactionViewHolder) {
            val transaction = transactions[position] as Transaction
            val context = holder.amount.context

            if (transaction.amount >= 0) {
                holder.amount.text = context.getString(R.string.positive_amount, transaction.amount)
                holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
            } else {
                holder.amount.text = context.getString(R.string.negative_amount, Math.abs(transaction.amount))
                holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
            }

            holder.label.text = transaction.label
            holder.date.text = dateFormat.format(transaction.date)

            holder.itemView.setOnClickListener {
                val intent = Intent(context, DetailedActivity::class.java)
                intent.putExtra("transaction", transaction)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    
    /**
     * Updates the adapter's data source.
     *
     * Prepares the list of transactions by inserting date headers,
     * then refreshes the RecyclerView to display the updated list.
     *
     * @param transactions The new list of transactions to display.
     */

    fun setData(transactions: List<Transaction>) {
        this.transactions = prepareData(transactions)
        notifyDataSetChanged()
    }


    /**
     * Prepares a list of transactions with date headers.
     *
     * Sorts transactions in descending order by date,
     * and inserts a Date object before the first transaction of each new day.
     *
     * @param transactions The list of transactions to prepare.
     * @return A list containing both Date headers and Transaction items.
     */
    private fun prepareData(transactions: List<Transaction>): List<Any> {
        // If the transaction list is empty, return it as is
        if (transactions.isEmpty()) return transactions

        // Sort transactions by date (newest first)
        val sortedTransactions = transactions.sortedByDescending { it.date }

        val result = mutableListOf<Any>()
        var lastDate: Date? = null

        for (transaction in sortedTransactions) {
            val date = transaction.date

            // If this transaction's date is different from the last one, add a new date header
            if (lastDate == null || !isSameDay(lastDate, date)) {
                result.add(date)
                lastDate = date
            }

            // Add the transaction itself
            result.add(transaction)
        }

        return result
    }



    /**
     * Checks if two dates are on the same day.
     */

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        calendar1.time = date1
        calendar2.time = date2
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }


    /**
     * ViewHolder for date headers.
     */

    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.dateText)
    }


     /**
     * ViewHolder for transaction items.
     */
    
    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.label)
        val amount: TextView = view.findViewById(R.id.amount)
        val date: TextView = view.findViewById(R.id.date)
    }
}
