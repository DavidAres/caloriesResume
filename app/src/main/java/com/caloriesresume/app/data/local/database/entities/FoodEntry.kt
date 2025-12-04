package com.caloriesresume.app.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.caloriesresume.app.data.local.database.converters.NutritionConverter

@Entity(tableName = "food_entries")
@TypeConverters(NutritionConverter::class)
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String,
    val nutritionData: NutritionData,
    val foodType: String? = null,
    val foodGroups: String? = null,
    val recipes: String? = null,
    val mealEstimation: String? = null
)

data class NutritionData(
    val calories: Double,
    val protein: Double,
    val carbohydrates: Double,
    val fat: Double,
    val fiber: Double?,
    val sugar: Double?,
    val sodium: Double?,
    val calcium: Double?,
    val iron: Double?,
    val phosphorus: Double?,
    val potassium: Double?,
    val vitaminA: Double?,
    val vitaminC: Double?,
    val ingredients: List<String> = emptyList()
)

