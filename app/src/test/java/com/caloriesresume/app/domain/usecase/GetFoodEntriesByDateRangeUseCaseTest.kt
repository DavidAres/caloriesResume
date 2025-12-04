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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetFoodEntriesByDateRangeUseCaseTest {

    @Mock
    private lateinit var repository: IFoodRepository

    private lateinit var useCase: GetFoodEntriesByDateRangeUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = GetFoodEntriesByDateRangeUseCase(repository)
    }

    @Test
    fun `invoke should return flow of food entries in date range`() = runTest {
        val startTime = 1000L
        val endTime = 2000L
        val expectedEntries = listOf(
            FoodEntry(
                id = 1L,
                timestamp = 1500L,
                imageUri = "content://test",
                nutritionData = NutritionData(calories = 500.0, protein = 25.0, carbohydrates = 50.0, fat = 20.0)
            )
        )

        whenever(repository.getFoodEntriesByDateRange(startTime, endTime))
            .thenReturn(flowOf(expectedEntries))

        useCase(startTime, endTime).test {
            val result = awaitItem()
            assertEquals(expectedEntries, result)
            verify(repository).getFoodEntriesByDateRange(startTime, endTime)
            awaitComplete()
        }
    }
}


