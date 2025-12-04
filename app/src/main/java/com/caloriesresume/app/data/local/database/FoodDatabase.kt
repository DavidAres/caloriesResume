package com.caloriesresume.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.caloriesresume.app.data.local.database.dao.FoodEntryDao
import com.caloriesresume.app.data.local.database.entities.FoodEntry

@Database(
    entities = [FoodEntry::class],
    version = 2,
    exportSchema = false
)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodEntryDao(): FoodEntryDao
}

