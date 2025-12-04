package com.caloriesresume.app.data.mapper

import com.caloriesresume.app.data.local.database.entities.NutritionData
import com.caloriesresume.app.domain.model.NutritionInfo

fun NutritionInfo.toNutritionData(): NutritionData {
    return NutritionData(
        calories = calories,
        protein = protein,
        carbohydrates = carbohydrates,
        fat = fat,
        fiber = fiber,
        sugar = sugar,
        sodium = sodium,
        calcium = calcium,
        iron = iron,
        phosphorus = phosphorus,
        potassium = potassium,
        vitaminA = vitaminA,
        vitaminC = vitaminC,
        ingredients = ingredients
    )
}

fun NutritionData.toNutritionInfo(): NutritionInfo {
    return NutritionInfo(
        calories = calories,
        protein = protein,
        carbohydrates = carbohydrates,
        fat = fat,
        fiber = fiber,
        sugar = sugar,
        sodium = sodium,
        calcium = calcium,
        iron = iron,
        phosphorus = phosphorus,
        potassium = potassium,
        vitaminA = vitaminA,
        vitaminC = vitaminC,
        ingredients = ingredients
    )
}

