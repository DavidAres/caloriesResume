package com.caloriesresume.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caloriesresume.app.data.local.database.entities.FoodEntry
import com.caloriesresume.app.domain.repository.IFoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FoodEntryDetailUiState(
    val foodEntry: FoodEntry? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class FoodEntryDetailViewModel @Inject constructor(
    private val repository: IFoodRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FoodEntryDetailUiState())
    val uiState: StateFlow<FoodEntryDetailUiState> = _uiState

    fun loadFoodEntry(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val entry = repository.getFoodEntryById(id).firstOrNull()
                _uiState.value = _uiState.value.copy(
                    foodEntry = entry,
                    isLoading = false,
                    error = if (entry == null) "No se encontró la entrada" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar la entrada"
                )
            }
        }
    }
}


