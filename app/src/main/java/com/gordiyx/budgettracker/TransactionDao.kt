package com.gordiyx.budgettracker

import androidx.room.*


/**
 * Data Access Object (DAO) for managing transactions in the Room database.
 *
 * Provides methods to query, insert, update, and delete Transaction entities.
 */

@Dao
interface TransactionDao {

    /**
     * Retrieves all transactions from the database.
     * @return A list of all Transaction objects.
     */

    @Query("SELECT * FROM transactions")
    fun getAll(): List<Transaction>


    /**
     * Inserts one or more transactions into the database.
     * @param transaction The Transaction(s) to insert.
     */

    @Insert
    fun insertAll(vararg transaction: Transaction)


    /**
     * Deletes a specific transaction from the database.
     * @param transaction The Transaction to delete.
     */

    @Delete
    fun delete(transaction: Transaction)


    /**
     * Updates one or more existing transactions in the database.
     * @param transaction The Transaction(s) to update.
     */
    
    @Update
    fun update(vararg transaction: Transaction)

}
