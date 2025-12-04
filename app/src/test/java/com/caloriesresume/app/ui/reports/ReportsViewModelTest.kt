package com.caloriesresume.app.ui.reports

import app.cash.turbine.test
import com.caloriesresume.app.data.local.database.entities.FoodEntry
import com.caloriesresume.app.data.local.database.entities.NutritionData
import com.caloriesresume.app.domain.usecase.CalculateNutritionSummaryUseCase
import com.caloriesresume.app.domain.usecase.GetFoodEntriesByDateRangeUseCase
import com.caloriesresume.app.domain.usecase.NutritionSummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {

    @Mock
    private lateinit var getFoodEntriesByDateRangeUseCase: GetFoodEntriesByDateRangeUseCase

    @Mock
    private lateinit var calculateNutritionSummaryUseCase: CalculateNutritionSummaryUseCase

    private lateinit var viewModel: ReportsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = ReportsViewModel(
            getFoodEntriesByDateRangeUseCase,
            calculateNutritionSummaryUseCase
        )
    }

    @Test
    fun `initial state should load daily report`() = runTest {
        val entries = listOf(
            FoodEntry(
                id = 1L,
                imageUri = "content://test",
                nutritionData = NutritionData(calories = 500.0, protein = 25.0, carbohydrates = 50.0, fat = 20.0)
            )
        )
        val summary = NutritionSummary(
            calories = 500.0,
            protein = 25.0,
            carbohydrates = 50.0,
            fat = 20.0,
            entryCount = 1
        )

        whenever(getFoodEntriesByDateRangeUseCase(any(), any()))
            .thenReturn(flowOf(entries))
        whenever(calculateNutritionSummaryUseCase(entries))
            .thenReturn(summary)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(ReportPeriod.DAILY, state.period)
        }
    }

    @Test
    fun `loadReport should update state with correct period and summary`() = runTest {
        val entries = listOf(
            FoodEntry(
                id = 1L,
                imageUri = "content://test",
                nutritionData = NutritionData(calories = 500.0, protein = 25.0, carbohydrates = 50.0, fat = 20.0)
            )
        )
        val summary = NutritionSummary(
            calories = 500.0,
            protein = 25.0,
            carbohydrates = 50.0,
            fat = 20.0,
            entryCount = 1
        )

        whenever(getFoodEntriesByDateRangeUseCase(any(), any()))
            .thenReturn(flowOf(entries))
        whenever(calculateNutritionSummaryUseCase(entries))
            .thenReturn(summary)

        viewModel.uiState.test {
            skipItems(1)
            
            viewModel.loadReport(ReportPeriod.WEEKLY)
            
            val loadingState = awaitItem()
            assertEquals(ReportPeriod.WEEKLY, loadingState.period)
            assertTrue(loadingState.isLoading)
            
            val successState = awaitItem()
            assertEquals(ReportPeriod.WEEKLY, successState.period)
            assertEquals(summary, successState.summary)
            assertFalse(successState.isLoading)
            assertNull(successState.error)
        }
    }

    @Test
    fun `getSummaryAsString should return formatted string`() {
        val summary = NutritionSummary(
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
            entryCount = 1
        )

        viewModel.uiState.value = ReportsUiState(summary = summary, period = ReportPeriod.DAILY)

        val result = viewModel.getSummaryAsString()

        assertTrue(result.contains("Resumen Nutricional"))
        assertTrue(result.contains("DAILY"))
        assertTrue(result.contains("500"))
        assertTrue(result.contains("25"))
        assertTrue(result.contains("1"))
    }
}
