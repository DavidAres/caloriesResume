package com.caloriesresume.app.domain.usecase

import android.net.Uri
import com.caloriesresume.app.domain.model.NutritionInfo
import com.caloriesresume.app.domain.repository.IFoodRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SaveFoodEntryUseCaseTest {

    @Mock
    private lateinit var repository: IFoodRepository

    private lateinit var useCase: SaveFoodEntryUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = SaveFoodEntryUseCase(repository)
    }

    @Test
    fun `invoke should call repository saveFoodEntry and return id`() = runTest {
        val imageUri = Uri.parse("content://test")
        val nutritionInfo = NutritionInfo(
            calories = 500.0,
            protein = 25.0,
            carbohydrates = 50.0,
            fat = 20.0
        )
        val expectedId = 1L

        whenever(repository.saveFoodEntry(imageUri, nutritionInfo, null))
            .thenReturn(expectedId)

        val result = useCase(imageUri, nutritionInfo, null)

        assertEquals(expectedId, result)
        verify(repository).saveFoodEntry(imageUri, nutritionInfo, null)
    }
}


