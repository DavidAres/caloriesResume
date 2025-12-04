package com.caloriesresume.app.ui.advice

import app.cash.turbine.test
import com.caloriesresume.app.domain.usecase.GetDietaryAdviceUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AdviceViewModelTest {

    @Mock
    private lateinit var getDietaryAdviceUseCase: GetDietaryAdviceUseCase

    private lateinit var viewModel: AdviceViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = AdviceViewModel(getDietaryAdviceUseCase)
    }

    @Test
    fun `initial state should be correct`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.advice)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `getDietaryAdvice should update state to loading then success`() = runTest {
        val summary = "Test summary"
        val apiKey = "test-api-key"
        val expectedAdvice = "Eat more vegetables and fruits"

        whenever(getDietaryAdviceUseCase(summary, apiKey))
            .thenReturn(Result.success(expectedAdvice))

        viewModel.uiState.test {
            viewModel.getDietaryAdvice(summary, apiKey)
            
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.error)
            
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(expectedAdvice, successState.advice)
            assertNull(successState.error)
        }
        
        verify(getDietaryAdviceUseCase).invoke(summary, apiKey)
    }

    @Test
    fun `getDietaryAdvice should update state to loading then error`() = runTest {
        val summary = "Test summary"
        val apiKey = "test-api-key"
        val errorMessage = "API Error"

        whenever(getDietaryAdviceUseCase(summary, apiKey))
            .thenReturn(Result.failure(Exception(errorMessage)))

        viewModel.uiState.test {
            viewModel.getDietaryAdvice(summary, apiKey)
            
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals(errorMessage, errorState.error)
            assertNull(errorState.advice)
        }
    }

    @Test
    fun `resetState should reset ui state`() = runTest {
        val summary = "Test summary"
        val apiKey = "test-api-key"
        val advice = "Eat more vegetables"

        whenever(repository.getDietaryAdvice(summary, apiKey))
            .thenReturn(Result.success(advice))

        viewModel.getDietaryAdvice(summary, apiKey)
        
        viewModel.uiState.test {
            skipItems(2)
            
            viewModel.resetState()
            
            val resetState = awaitItem()
            assertNull(resetState.advice)
            assertFalse(resetState.isLoading)
            assertNull(resetState.error)
        }
    }
}

