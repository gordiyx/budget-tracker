package com.gordiyx.budgettracker

import androidx.room.TypeConverter
import java.util.Date


/**
 * Converters for Room to handle non-primitive types.
 *
 * This class provides methods to convert Date objects to Long timestamps
 * and vice versa, enabling Room to store dates in the database.
 */

class Converters {

    /**
     * Converts a Long timestamp to a Date object.
     * @param value The timestamp to convert.
     * @return The corresponding Date object, or null if input is null.
     */
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }


    /**
     * Converts a Date object to a Long timestamp.
     * @param date The Date to convert.
     * @return The corresponding timestamp in milliseconds, or null if input is null.
     */

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
