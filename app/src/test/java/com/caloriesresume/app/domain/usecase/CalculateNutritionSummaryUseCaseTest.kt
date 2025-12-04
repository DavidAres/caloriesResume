package com.caloriesresume.app.domain.usecase

import com.caloriesresume.app.data.local.database.entities.FoodEntry
import com.caloriesresume.app.data.local.database.entities.NutritionData
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CalculateNutritionSummaryUseCaseTest {

    private lateinit var useCase: CalculateNutritionSummaryUseCase

    @Before
    fun setup() {
        useCase = CalculateNutritionSummaryUseCase()
    }

    @Test
    fun `invoke should return empty summary when entries list is empty`() {
        val entries = emptyList<FoodEntry>()

        val result = useCase(entries)

        assertEquals(0.0, result.calories, 0.01)
        assertEquals(0, result.entryCount)
    }

    @Test
    fun `invoke should calculate correct summary for single entry`() {
        val entries = listOf(
            FoodEntry(
                id = 1L,
                imageUri = "content://test",
                nutritionData = NutritionData(
                    calories = 500.0,
                    protein = 25.0,
                    carbohydrates = 50.0,
                    fat = 20.0,
                    fiber = 10.0,
                    iron = 5.0
                )
            )
        )

        val result = useCase(entries)

        assertEquals(500.0, result.calories, 0.01)
        assertEquals(25.0, result.protein, 0.01)
        assertEquals(50.0, result.carbohydrates, 0.01)
        assertEquals(20.0, result.fat, 0.01)
        assertEquals(10.0, result.fiber, 0.01)
        assertEquals(5.0, result.iron, 0.01)
        assertEquals(1, result.entryCount)
    }

    @Test
    fun `invoke should sum multiple entries correctly`() {
        val entries = listOf(
            FoodEntry(
                id = 1L,
                imageUri = "content://test1",
                nutritionData = NutritionData(
                    calories = 500.0,
                    protein = 25.0,
                    carbohydrates = 50.0,
                    fat = 20.0
                )
            ),
            FoodEntry(
                id = 2L,
                imageUri = "content://test2",
                nutritionData = NutritionData(
                    calories = 300.0,
                    protein = 15.0,
                    carbohydrates = 30.0,
                    fat = 10.0
                )
            )
        )

        val result = useCase(entries)

        assertEquals(800.0, result.calories, 0.01)
        assertEquals(40.0, result.protein, 0.01)
        assertEquals(80.0, result.carbohydrates, 0.01)
        assertEquals(30.0, result.fat, 0.01)
        assertEquals(2, result.entryCount)
    }

    @Test
    fun `invoke should handle null optional values correctly`() {
        val entries = listOf(
            FoodEntry(
                id = 1L,
                imageUri = "content://test",
                nutritionData = NutritionData(
                    calories = 500.0,
                    protein = 25.0,
                    carbohydrates = 50.0,
                    fat = 20.0,
                    fiber = null,
                    iron = null
                )
            )
        )

        val result = useCase(entries)

        assertEquals(0.0, result.fiber, 0.01)
        assertEquals(0.0, result.iron, 0.01)
    }
}


