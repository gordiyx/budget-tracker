package com.gordiyx.budgettracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Activity for displaying income and expense statistics using a bar chart.
 * Also provides bottom navigation to switch between screens.
 */

class ChartActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var barChart: BarChart
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        // Initialize UI elements
        barChart = findViewById(R.id.barChart)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.navigation_chart

        // Initialize database
        db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()

        // Fetch and display data
        fetchAllData()
    }


    /**
     * Fetches all transactions from the database,
     * calculates income and expenses, and displays them on the chart.
     */
    
    private fun fetchAllData() {
        GlobalScope.launch {
            val transactions = db.transactionDao().getAll()

            // Separate income and expenses
            val income = transactions.filter { it.amount > 0 }.map { it.amount }.sum()
            val expense = transactions.filter { it.amount < 0 }.map { it.amount }.sum()

            // Create bar entries
            val incomeEntry = BarEntry(0f, income.toFloat())
            val expenseEntry = BarEntry(1f, Math.abs(expense.toFloat()))

            val dataSet = BarDataSet(listOf(incomeEntry, expenseEntry), "Income vs Expenses").apply {
                colors = listOf(
                    Color.parseColor("#4CAF50"), // Green for income
                    Color.parseColor("#F44336")  // Red for expenses
                )
                valueTextColor = Color.BLACK
                valueTextSize = 16f
            }

            val data = BarData(dataSet)

            runOnUiThread {
                // Set data and refresh the chart
                barChart.data = data
                barChart.invalidate()


                // General chart appearance settings
                barChart.setFitBars(true)                       // Adjusts the bar width to fit the X-axis properly
                barChart.setDrawGridBackground(false)          // Disable grid background behind the bars
                barChart.setDrawBarShadow(false)               // Do not draw a shadow behind the bars
                barChart.setDrawValueAboveBar(true)            // Display values above the bars
                barChart.legend.isEnabled = false              // Disable the chart legend
                barChart.animateY(1000)                        // Animate the bars vertically over 1 second


                // Configure X Axis (horizontal axis at the bottom)
                val xAxis = barChart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.setDrawAxisLine(true)
                xAxis.textColor = Color.BLACK
                xAxis.textSize = 12f
                xAxis.granularity = 1f
                // Custom formatter to show Income and Expenses on X-axis
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return if (value == 0f) "Income" else "Expenses"
                    }
                }

                // Configure Left Axis (vertical axis on the left side)
                val leftAxis = barChart.axisLeft
                leftAxis.setDrawGridLines(true)
                leftAxis.setDrawAxisLine(true)
                leftAxis.textColor = Color.BLACK
                leftAxis.textSize = 12f
                leftAxis.axisMinimum = 0f

                // Configure Right Axis (vertical axis on the right side)
                val rightAxis = barChart.axisRight
                rightAxis.setDrawGridLines(false)
                rightAxis.setDrawAxisLine(false)
                rightAxis.setDrawLabels(false)

                // Remove the default chart description text (bottom right corner)
                val description = Description()
                description.text = ""
                barChart.description = description

                // Set chart background color to transparent
                barChart.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }


    /**
     * Handles bottom navigation item selection.
     */

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.navigation_chart -> {
                true    // Stay on the current screen
            }
            else -> false
        }
    }
}
