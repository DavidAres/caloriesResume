package com.caloriesresume.app.domain.model

data class NutritionInfo(
    val calories: Double,
    val protein: Double,
    val carbohydrates: Double,
    val fat: Double,
    val fiber: Double? = null,
    val sugar: Double? = null,
    val sodium: Double? = null,
    val calcium: Double? = null,
    val iron: Double? = null,
    val phosphorus: Double? = null,
    val potassium: Double? = null,
    val vitaminA: Double? = null,
    val vitaminC: Double? = null,
    val ingredients: List<String> = emptyList()
)




