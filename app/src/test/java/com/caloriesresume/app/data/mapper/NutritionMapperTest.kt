package com.caloriesresume.app.data.mapper

import com.caloriesresume.app.data.local.database.entities.NutritionData
import com.caloriesresume.app.data.remote.dto.Nutrition
import com.caloriesresume.app.data.remote.dto.NutritionValue
import com.caloriesresume.app.domain.model.NutritionInfo
import org.junit.Assert.*
import org.junit.Test

class NutritionMapperTest {

    @Test
    fun `toNutritionInfo should map Nutrition to NutritionInfo correctly`() {
        val nutrition = Nutrition(
            calories = NutritionValue(500.0, "kcal", 0.95),
            protein = NutritionValue(25.0, "g", 0.90),
            carbs = NutritionValue(50.0, "g", 0.85),
            fat = NutritionValue(20.0, "g", 0.88),
            fiber = NutritionValue(10.0, "g", 0.80),
            sugar = NutritionValue(15.0, "g", 0.75),
            sodium = NutritionValue(500.0, "mg", 0.70),
            calcium = NutritionValue(200.0, "mg", 0.65),
            iron = NutritionValue(5.0, "mg", 0.60),
            phosphorus = NutritionValue(300.0, "mg", 0.55),
            potassium = NutritionValue(400.0, "mg", 0.50),
            vitaminA = NutritionValue(1000.0, "IU", 0.45),
            vitaminC = NutritionValue(50.0, "mg", 0.40)
        )
        val ingredients = listOf("Tomato", "Lettuce", "Chicken")

        val result = nutrition.toNutritionInfo(ingredients)

        assertEquals(500.0, result.calories, 0.01)
        assertEquals(25.0, result.protein, 0.01)
        assertEquals(50.0, result.carbohydrates, 0.01)
        assertEquals(20.0, result.fat, 0.01)
        assertEquals(10.0, result.fiber, 0.01)
        assertEquals(15.0, result.sugar, 0.01)
        assertEquals(500.0, result.sodium, 0.01)
        assertEquals(200.0, result.calcium, 0.01)
        assertEquals(5.0, result.iron, 0.01)
        assertEquals(300.0, result.phosphorus, 0.01)
        assertEquals(400.0, result.potassium, 0.01)
        assertEquals(1000.0, result.vitaminA, 0.01)
        assertEquals(50.0, result.vitaminC, 0.01)
        assertEquals(ingredients, result.ingredients)
    }

    @Test
    fun `toNutritionInfo should handle null values correctly`() {
        val nutrition = Nutrition(
            calories = NutritionValue(500.0, "kcal", 0.95),
            protein = NutritionValue(25.0, "g", 0.90),
            carbs = NutritionValue(50.0, "g", 0.85),
            fat = NutritionValue(20.0, "g", 0.88),
            fiber = null,
            sugar = null,
            sodium = null,
            calcium = null,
            iron = null,
            phosphorus = null,
            potassium = null,
            vitaminA = null,
            vitaminC = null
        )

        val result = nutrition.toNutritionInfo()

        assertEquals(500.0, result.calories, 0.01)
        assertNull(result.fiber)
        assertNull(result.sugar)
        assertNull(result.sodium)
    }

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
        assertEquals(10.0, result.fiber, 0.01)
        assertEquals(15.0, result.sugar, 0.01)
        assertEquals(500.0, result.sodium, 0.01)
        assertEquals(200.0, result.calcium, 0.01)
        assertEquals(5.0, result.iron, 0.01)
        assertEquals(300.0, result.phosphorus, 0.01)
        assertEquals(400.0, result.potassium, 0.01)
        assertEquals(1000.0, result.vitaminA, 0.01)
        assertEquals(50.0, result.vitaminC, 0.01)
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
        assertEquals(10.0, result.fiber, 0.01)
        assertEquals(15.0, result.sugar, 0.01)
        assertEquals(500.0, result.sodium, 0.01)
        assertEquals(200.0, result.calcium, 0.01)
        assertEquals(5.0, result.iron, 0.01)
        assertEquals(300.0, result.phosphorus, 0.01)
        assertEquals(400.0, result.potassium, 0.01)
        assertEquals(1000.0, result.vitaminA, 0.01)
        assertEquals(50.0, result.vitaminC, 0.01)
        assertEquals(listOf("Tomato", "Lettuce"), result.ingredients)
    }
}


