package com.caloriesresume.app.data.mapper

import com.caloriesresume.app.data.local.database.entities.NutritionData
import com.caloriesresume.app.domain.model.NutritionInfo
import org.junit.Assert.*
import org.junit.Test

class NutritionMapperTest {

    @Test
    fun `toNutritionData should map NutritionInfo to NutritionData correctly`() {
        val nutritionInfo = NutritionInfo(
            calories = 500.0,
            protein = 25.0,
            carbohydrates = 50.0,
            fat = 20.0,
            fiber = 10.0,
            sugar = 15.0,
            sodium = 500.0,
            calcium = 200.0,
            iron = 5.0,
            phosphorus = 300.0,
            potassium = 400.0,
            vitaminA = 1000.0,
            vitaminC = 50.0,
            ingredients = listOf("Tomato", "Lettuce")
        )

        val result = nutritionInfo.toNutritionData()

        assertEquals(500.0, result.calories, 0.01)
        assertEquals(25.0, result.protein, 0.01)
        assertEquals(50.0, result.carbohydrates, 0.01)
        assertEquals(20.0, result.fat, 0.01)
        assertEquals(10.0, result.fiber ?: 0.0, 0.01)
        assertEquals(15.0, result.sugar ?: 0.0, 0.01)
        assertEquals(500.0, result.sodium ?: 0.0, 0.01)
        assertEquals(200.0, result.calcium ?: 0.0, 0.01)
        assertEquals(5.0, result.iron ?: 0.0, 0.01)
        assertEquals(300.0, result.phosphorus ?: 0.0, 0.01)
        assertEquals(400.0, result.potassium ?: 0.0, 0.01)
        assertEquals(1000.0, result.vitaminA ?: 0.0, 0.01)
        assertEquals(50.0, result.vitaminC ?: 0.0, 0.01)
        assertEquals(listOf("Tomato", "Lettuce"), result.ingredients)
    }

    @Test
    fun `toNutritionInfo should map NutritionData to NutritionInfo correctly`() {
        val nutritionData = NutritionData(
            calories = 500.0,
            protein = 25.0,
            carbohydrates = 50.0,
            fat = 20.0,
            fiber = 10.0,
            sugar = 15.0,
            sodium = 500.0,
            calcium = 200.0,
            iron = 5.0,
            phosphorus = 300.0,
            potassium = 400.0,
            vitaminA = 1000.0,
            vitaminC = 50.0,
            ingredients = listOf("Tomato", "Lettuce")
        )

        val result = nutritionData.toNutritionInfo()

        assertEquals(500.0, result.calories, 0.01)
        assertEquals(25.0, result.protein, 0.01)
        assertEquals(50.0, result.carbohydrates, 0.01)
        assertEquals(20.0, result.fat, 0.01)
        assertEquals(10.0, result.fiber ?: 0.0, 0.01)
        assertEquals(15.0, result.sugar ?: 0.0, 0.01)
        assertEquals(500.0, result.sodium ?: 0.0, 0.01)
        assertEquals(200.0, result.calcium ?: 0.0, 0.01)
        assertEquals(5.0, result.iron ?: 0.0, 0.01)
        assertEquals(300.0, result.phosphorus ?: 0.0, 0.01)
        assertEquals(400.0, result.potassium ?: 0.0, 0.01)
        assertEquals(1000.0, result.vitaminA ?: 0.0, 0.01)
        assertEquals(50.0, result.vitaminC ?: 0.0, 0.01)
        assertEquals(listOf("Tomato", "Lettuce"), result.ingredients)
    }
}


