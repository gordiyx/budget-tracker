package com.alterpat.budgettracker

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

class ChartActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var barChart: BarChart
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        barChart = findViewById(R.id.barChart)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.navigation_chart
        db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()

        fetchAllData()
    }

    private fun fetchAllData() {
        GlobalScope.launch {
            val transactions = db.transactionDao().getAll()

            val income = transactions.filter { it.amount > 0 }.map { it.amount }.sum()
            val expense = transactions.filter { it.amount < 0 }.map { it.amount }.sum()

            val incomeEntry = BarEntry(0f, income.toFloat())
            val expenseEntry = BarEntry(1f, Math.abs(expense.toFloat()))

            val dataSet = BarDataSet(listOf(incomeEntry, expenseEntry), "Доходи та Витрати").apply {
                colors = listOf(Color.parseColor("#4CAF50"), Color.parseColor("#F44336"))
                valueTextColor = Color.BLACK
                valueTextSize = 16f
            }
            val data = BarData(dataSet)

            runOnUiThread {
                barChart.data = data
                barChart.invalidate()


                barChart.setFitBars(true)
                barChart.setDrawGridBackground(false)
                barChart.setDrawBarShadow(false)
                barChart.setDrawValueAboveBar(true)
                barChart.legend.isEnabled = false // Отключаем легенду
                barChart.animateY(1000)


                val xAxis = barChart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.setDrawAxisLine(true)
                xAxis.textColor = Color.BLACK
                xAxis.textSize = 12f
                xAxis.granularity = 1f
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return if (value == 0f) "Доходи" else "Витрати"
                    }
                }


                val leftAxis = barChart.axisLeft
                leftAxis.setDrawGridLines(true)
                leftAxis.setDrawAxisLine(true)
                leftAxis.textColor = Color.BLACK
                leftAxis.textSize = 12f
                leftAxis.axisMinimum = 0f


                val rightAxis = barChart.axisRight
                rightAxis.setDrawGridLines(false)
                rightAxis.setDrawAxisLine(false)
                rightAxis.setDrawLabels(false)


                val description = Description()
                description.text = ""
                barChart.description = description


                barChart.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.navigation_chart -> {
                true
            }
            else -> false
        }
    }
}
