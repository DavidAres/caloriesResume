package com.caloriesresume.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caloriesresume.app.data.local.database.entities.FoodEntry
import com.caloriesresume.app.domain.usecase.DeleteFoodEntryUseCase
import com.caloriesresume.app.domain.usecase.GetAllFoodEntriesUseCase
import com.caloriesresume.app.domain.usecase.GetFoodEntriesByDateRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val foodEntries: List<FoodEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAllFoodEntriesUseCase: GetAllFoodEntriesUseCase,
    private val deleteFoodEntryUseCase: DeleteFoodEntryUseCase,
    private val getFoodEntriesByDateRangeUseCase: GetFoodEntriesByDateRangeUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        loadFoodEntries()
    }

    private fun loadFoodEntries() {
        viewModelScope.launch {
            getAllFoodEntriesUseCase().collect { entries ->
                _uiState.value = _uiState.value.copy(
                    foodEntries = entries,
                    isLoading = false
                )
            }
        }
    }

    fun deleteFoodEntry(id: Long) {
        viewModelScope.launch {
            deleteFoodEntryUseCase(id)
        }
    }

    fun filterByDateRange(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getFoodEntriesByDateRangeUseCase(startTime, endTime).collect { entries ->
                _uiState.value = _uiState.value.copy(
                    foodEntries = entries,
                    isLoading = false
                )
            }
        }
    }

    fun resetFilter() {
        loadFoodEntries()
    }
}

