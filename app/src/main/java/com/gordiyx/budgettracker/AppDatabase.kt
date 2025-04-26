package com.gordiyx.budgettracker

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


/**
 * Main Room database for the Budget Tracker app.
 * 
 * - Defines all entities and DAOs.
 * - Provides access to the TransactionDao for CRUD operations.
 * - Uses TypeConverters for custom data types (e.g., Date).
 */

 @Database(
    entities = [Transaction::class],
    version = 2
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // Provides access to transaction-related database operations.
    abstract fun transactionDao(): TransactionDao
}
