package com.caloriesresume.app.ui.history

import app.cash.turbine.test
import com.caloriesresume.app.data.local.database.entities.FoodEntry
import com.caloriesresume.app.data.local.database.entities.NutritionData
import com.caloriesresume.app.domain.usecase.DeleteFoodEntryUseCase
import com.caloriesresume.app.domain.usecase.GetAllFoodEntriesUseCase
import com.caloriesresume.app.domain.usecase.GetFoodEntriesByDateRangeUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @Mock
    private lateinit var getAllFoodEntriesUseCase: GetAllFoodEntriesUseCase

    @Mock
    private lateinit var deleteFoodEntryUseCase: DeleteFoodEntryUseCase

    @Mock
    private lateinit var getFoodEntriesByDateRangeUseCase: GetFoodEntriesByDateRangeUseCase

    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = HistoryViewModel(
            getAllFoodEntriesUseCase,
            deleteFoodEntryUseCase,
            getFoodEntriesByDateRangeUseCase
        )
    }

    @Test
    fun `initial state should load food entries`() = runTest {
        val entries = listOf(
            FoodEntry(
                id = 1L,
                imageUri = "content://test1",
                nutritionData = NutritionData(calories = 500.0, protein = 25.0, carbohydrates = 50.0, fat = 20.0)
            )
        )

        whenever(getAllFoodEntriesUseCase())
            .thenReturn(flowOf(entries))

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(entries, state.foodEntries)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `deleteFoodEntry should call repository`() = runTest {
        val id = 1L

        viewModel.deleteFoodEntry(id)

        verify(deleteFoodEntryUseCase).invoke(id)
    }

    @Test
    fun `filterByDateRange should update state with filtered entries`() = runTest {
        val startTime = 1000L
        val endTime = 2000L
        val filteredEntries = listOf(
            FoodEntry(
                id = 1L,
                timestamp = 1500L,
                imageUri = "content://test",
                nutritionData = NutritionData(calories = 500.0, protein = 25.0, carbohydrates = 50.0, fat = 20.0)
            )
        )

        whenever(getFoodEntriesByDateRangeUseCase(startTime, endTime))
            .thenReturn(flowOf(filteredEntries))

        viewModel.uiState.test {
            skipItems(1)
            
            viewModel.filterByDateRange(startTime, endTime)
            
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            val filteredState = awaitItem()
            assertEquals(filteredEntries, filteredState.foodEntries)
            assertFalse(filteredState.isLoading)
        }
    }

    @Test
    fun `resetFilter should reload all entries`() = runTest {
        val entries = listOf(
            FoodEntry(
                id = 1L,
                imageUri = "content://test",
                nutritionData = NutritionData(calories = 500.0, protein = 25.0, carbohydrates = 50.0, fat = 20.0)
            )
        )

        whenever(getAllFoodEntriesUseCase())
            .thenReturn(flowOf(entries))

        viewModel.uiState.test {
            skipItems(1)
            
            viewModel.resetFilter()
            
            val state = awaitItem()
            assertEquals(entries, state.foodEntries)
        }
    }
}
