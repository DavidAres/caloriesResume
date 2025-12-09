package com.caloriesresume.app.domain.usecase

import com.caloriesresume.app.domain.repository.IFoodRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetDietaryAdviceUseCaseTest {

    @Mock
    private lateinit var repository: IFoodRepository

    private lateinit var useCase: GetDietaryAdviceUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = GetDietaryAdviceUseCase(repository)
    }

    @Test
    fun `invoke should return success result when repository succeeds`() = runTest {
        val summary = "Test summary"
        val apiKey = "test-api-key"
        val expectedAdvice = "Eat more vegetables"

        whenever(repository.getDietaryAdvice(summary, apiKey))
            .thenReturn(Result.success(expectedAdvice))

        val result = useCase(summary, apiKey)

        assertTrue(result.isSuccess)
        assertEquals(expectedAdvice, result.getOrNull())
        verify(repository).getDietaryAdvice(summary, apiKey)
    }

    @Test
    fun `invoke should return failure result when repository fails`() = runTest {
        val summary = "Test summary"
        val apiKey = "test-api-key"
        val exception = Exception("API Error")

        whenever(repository.getDietaryAdvice(summary, apiKey))
            .thenReturn(Result.failure(exception))

        val result = useCase(summary, apiKey)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}




