package com.gordiyx.budgettracker

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Main screen displaying a list of transactions and a financial dashboard.
 */

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var transactions: MutableList<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup BottomNavigation
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.navigation_home

        // Initialize RecyclerView components
        transactions = mutableListOf()
        transactionAdapter = TransactionAdapter(transactions)
        linearLayoutManager = LinearLayoutManager(this)

        recyclerview.apply {
            adapter = transactionAdapter
            layoutManager = linearLayoutManager
        }

        // Initialize database
        db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()

        // Fetch and display transactions
        fetchAll()

        // Handle Add button click
        addBtn.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }


    /**
     * Handles BottomNavigation item selection.
     */

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_home -> {
                true    // Already on home screen
            }
            R.id.navigation_chart -> {
                // Navigate to ChartActivity
                val intent = Intent(this, ChartActivity::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }
    }


    /**
     * Fetches all transactions from the database and updates the UI.
     */

    private fun fetchAll() {
        GlobalScope.launch {
            transactions = db.transactionDao().getAll().toMutableList()

            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }


    /**
     * Updates the financial summary dashboard with current totals.
     */

    private fun updateDashboard() {
        val totalAmount = transactions.map { it.amount }.sum()
        val budgetAmount = transactions.filter { it.amount > 0 }.map { it.amount }.sum()
        val expenseAmount = transactions.filter { it.amount < 0 }.map { it.amount }.sum()

        balance.text = "₴ %.2f".format(totalAmount)
        income.text = "↑ %.2f".format(budgetAmount)
        expense.text = "↓ %.2f".format(Math.abs(expenseAmount))
    }


    /**
     * Refreshes data every time the user returns to the screen.
     */
    
    override fun onResume() {
        super.onResume()
        fetchAll()
    }
}
