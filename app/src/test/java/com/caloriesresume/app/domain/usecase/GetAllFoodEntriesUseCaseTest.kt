package com.caloriesresume.app.domain.usecase

import app.cash.turbine.test
import com.caloriesresume.app.data.local.database.entities.FoodEntry
import com.caloriesresume.app.data.local.database.entities.NutritionData
import com.caloriesresume.app.domain.repository.IFoodRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetAllFoodEntriesUseCaseTest {

    @Mock
    private lateinit var repository: IFoodRepository

    private lateinit var useCase: GetAllFoodEntriesUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = GetAllFoodEntriesUseCase(repository)
    }

    @Test
    fun `invoke should return flow of food entries`() = runTest {
        val expectedEntries = listOf(
            FoodEntry(
                id = 1L,
                imageUri = "content://test1",
                nutritionData = NutritionData(
                    calories = 500.0,
                    protein = 25.0,
                    carbohydrates = 50.0,
                    fat = 20.0,
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
            ),
            FoodEntry(
                id = 2L,
                imageUri = "content://test2",
                nutritionData = NutritionData(
                    calories = 300.0,
                    protein = 15.0,
                    carbohydrates = 30.0,
                    fat = 10.0,
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
            )
        )

        whenever(repository.getAllFoodEntries())
            .thenReturn(flowOf(expectedEntries))

        useCase().test {
            val result = awaitItem()
            assertEquals(expectedEntries, result)
            awaitComplete()
        }
    }
}


