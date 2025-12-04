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
import org.mockito.kotlin.whenever

class AnalyzeFoodImageUseCaseTest {

    @Mock
    private lateinit var repository: IFoodRepository

    private lateinit var useCase: AnalyzeFoodImageUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = AnalyzeFoodImageUseCase(repository)
    }

    @Test
    fun `invoke should return success result when repository succeeds`() = runTest {
        val imageUri = Uri.parse("content://test")
        val apiKey = "test-api-key"
        val expectedNutritionInfo = NutritionInfo(
            calories = 500.0,
            protein = 25.0,
            carbohydrates = 50.0,
            fat = 20.0
        )

        whenever(repository.analyzeFoodImage(imageUri, apiKey))
            .thenReturn(Result.success(expectedNutritionInfo))

        val result = useCase(imageUri, apiKey)

        assertTrue(result.isSuccess)
        assertEquals(expectedNutritionInfo, result.getOrNull())
    }

    @Test
    fun `invoke should return failure result when repository fails`() = runTest {
        val imageUri = Uri.parse("content://test")
        val apiKey = "test-api-key"
        val exception = Exception("API Error")

        whenever(repository.analyzeFoodImage(imageUri, apiKey))
            .thenReturn(Result.failure(exception))

        val result = useCase(imageUri, apiKey)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}


