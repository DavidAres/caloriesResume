package com.caloriesresume.app.ui.advice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caloriesresume.app.domain.usecase.GetDietaryAdviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdviceUiState(
    val advice: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AdviceViewModel @Inject constructor(
    private val getDietaryAdviceUseCase: GetDietaryAdviceUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdviceUiState())
    val uiState: StateFlow<AdviceUiState> = _uiState

    fun getDietaryAdvice(nutritionSummary: String, apiKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            getDietaryAdviceUseCase(nutritionSummary, apiKey)
                .onSuccess { advice ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        advice = advice,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al obtener consejos"
                    )
                }
        }
    }

    fun resetState() {
        _uiState.value = AdviceUiState()
    }
}

