package com.caloriesresume.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caloriesresume.app.data.local.database.entities.FoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(foodEntry: FoodEntry): Long

    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllFoodEntries(): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries WHERE id = :id")
    suspend fun getFoodEntryById(id: Long): FoodEntry?

    @Query("SELECT * FROM food_entries WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getFoodEntriesByDateRange(startTime: Long, endTime: Long): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries WHERE date(timestamp/1000, 'unixepoch') = date(:date/1000, 'unixepoch') ORDER BY timestamp DESC")
    fun getFoodEntriesByDate(date: Long): Flow<List<FoodEntry>>

    @Query("DELETE FROM food_entries WHERE id = :id")
    suspend fun deleteFoodEntry(id: Long)

    @Query("DELETE FROM food_entries")
    suspend fun deleteAllFoodEntries()
}


