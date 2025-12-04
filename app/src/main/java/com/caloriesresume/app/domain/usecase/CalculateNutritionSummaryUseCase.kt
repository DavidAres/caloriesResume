package com.caloriesresume.app.domain.usecase

import com.caloriesresume.app.data.local.database.entities.FoodEntry
import javax.inject.Inject

data class NutritionSummary(
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0,
    val sugar: Double = 0.0,
    val sodium: Double = 0.0,
    val calcium: Double = 0.0,
    val iron: Double = 0.0,
    val phosphorus: Double = 0.0,
    val potassium: Double = 0.0,
    val vitaminA: Double = 0.0,
    val vitaminC: Double = 0.0,
    val entryCount: Int = 0
)

class CalculateNutritionSummaryUseCase @Inject constructor() {
    operator fun invoke(entries: List<FoodEntry>): NutritionSummary {
        if (entries.isEmpty()) {
            return NutritionSummary()
        }

        return entries.fold(NutritionSummary()) { acc, entry ->
            val nutrition = entry.nutritionData
            NutritionSummary(
                calories = acc.calories + nutrition.calories,
                protein = acc.protein + nutrition.protein,
                carbohydrates = acc.carbohydrates + nutrition.carbohydrates,
                fat = acc.fat + nutrition.fat,
                fiber = acc.fiber + (nutrition.fiber ?: 0.0),
                sugar = acc.sugar + (nutrition.sugar ?: 0.0),
                sodium = acc.sodium + (nutrition.sodium ?: 0.0),
                calcium = acc.calcium + (nutrition.calcium ?: 0.0),
                iron = acc.iron + (nutrition.iron ?: 0.0),
                phosphorus = acc.phosphorus + (nutrition.phosphorus ?: 0.0),
                potassium = acc.potassium + (nutrition.potassium ?: 0.0),
                vitaminA = acc.vitaminA + (nutrition.vitaminA ?: 0.0),
                vitaminC = acc.vitaminC + (nutrition.vitaminC ?: 0.0),
                entryCount = acc.entryCount + 1
            )
        }
    }
}


