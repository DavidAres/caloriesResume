package com.caloriesresume.app.domain.repository

import android.net.Uri
import com.caloriesresume.app.domain.model.NutritionInfo
import kotlinx.coroutines.flow.Flow

interface IFoodRepository {
    fun getAllFoodEntries(): Flow<List<com.caloriesresume.app.data.local.database.entities.FoodEntry>>
    fun getFoodEntryById(id: Long): Flow<com.caloriesresume.app.data.local.database.entities.FoodEntry?>
    fun getFoodEntriesByDateRange(startTime: Long, endTime: Long): Flow<List<com.caloriesresume.app.data.local.database.entities.FoodEntry>>
    fun getFoodEntriesByDate(date: Long): Flow<List<com.caloriesresume.app.data.local.database.entities.FoodEntry>>
    suspend fun segmentFoodImage(imageUri: Uri, apiKey: String): Result<com.caloriesresume.app.domain.model.SegmentationResult>
    suspend fun confirmDishAndGetNutrition(imageId: Int, dishId: Int, foodItemPosition: Int, apiKey: String): Result<Pair<NutritionInfo, List<String>>>
    suspend fun saveFoodEntry(imageUri: Uri, nutritionInfo: NutritionInfo, dishName: String?, logMealResponse: com.caloriesresume.app.domain.model.LogMealResponse?): Long
    suspend fun deleteFoodEntry(id: Long)
    suspend fun getDietaryAdvice(nutritionSummary: String, apiKey: String): Result<String>
}

