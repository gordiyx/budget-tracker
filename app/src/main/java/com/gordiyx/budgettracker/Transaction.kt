package com.gordiyx.budgettracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date


/**
 * Represents a single financial transaction (income or expense).
 *
 * This entity is stored in the "transactions" table in the Room database.
 * Implements Serializable to allow passing instances between activities via Intents.
 */

 @Entity(tableName = "transactions")
 data class Transaction(
     @PrimaryKey(autoGenerate = true)
     val id: Int,                // Unique transaction ID, auto-generated
     val label: String,          // Transaction label 
     val amount: Double,         // Transaction amount (positive for income, negative for expense)
     val date: Date,             // Date of the transaction
     val description: String     // Additional notes about the transaction
 ) : Serializable
