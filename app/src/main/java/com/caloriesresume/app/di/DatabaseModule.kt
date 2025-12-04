package com.caloriesresume.app.di

import android.content.Context
import androidx.room.Room
import com.caloriesresume.app.data.local.database.FoodDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideFoodDatabase(@ApplicationContext context: Context): FoodDatabase {
        return Room.databaseBuilder(
            context,
            FoodDatabase::class.java,
            "food_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFoodEntryDao(database: FoodDatabase) = database.foodEntryDao()
}


