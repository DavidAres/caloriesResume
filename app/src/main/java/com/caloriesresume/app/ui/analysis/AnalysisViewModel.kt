package com.caloriesresume.app.ui.analysis

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caloriesresume.app.domain.model.NutritionInfo
import com.caloriesresume.app.domain.model.SegmentationResult
import com.caloriesresume.app.domain.model.DishCandidate
import com.caloriesresume.app.domain.usecase.SegmentFoodImageUseCase
import com.caloriesresume.app.domain.usecase.ConfirmDishUseCase
import com.caloriesresume.app.domain.usecase.SaveFoodEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalysisUiState(
    val isLoading: Boolean = false,
    val segmentationResult: SegmentationResult? = null,
    val selectedCandidate: DishCandidate? = null,
    val nutritionInfo: NutritionInfo? = null,
    val ingredients: List<String> = emptyList(),
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val segmentFoodImageUseCase: SegmentFoodImageUseCase,
    private val confirmDishUseCase: ConfirmDishUseCase,
    private val saveFoodEntryUseCase: SaveFoodEntryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState: StateFlow<AnalysisUiState> = _uiState

    fun segmentImage(imageUri: Uri, apiKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            segmentFoodImageUseCase(imageUri, apiKey)
                .onSuccess { segmentationResult ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        segmentationResult = segmentationResult,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al analizar la imagen"
                    )
                }
        }
    }

    fun selectCandidate(candidate: DishCandidate, apiKey: String) {
        viewModelScope.launch {
            val segmentationResult = _uiState.value.segmentationResult
            if (segmentationResult != null) {
                android.util.Log.d("AnalysisViewModel", "Seleccionando candidato - imageId: ${segmentationResult.imageId}, dishId: ${candidate.dishId}, foodItemPosition: ${candidate.foodItemPosition}")
                
                _uiState.value = _uiState.value.copy(
                    selectedCandidate = candidate,
                    isLoading = true
                )
                
                confirmDishUseCase(
                    imageId = segmentationResult.imageId,
                    dishId = candidate.dishId,
                    foodItemPosition = candidate.foodItemPosition,
                    apiKey = apiKey
                )
                    .onSuccess { (nutritionInfo, ingredients) ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            nutritionInfo = nutritionInfo,
                            ingredients = ingredients,
                            error = null
                        )
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al confirmar el plato"
                        )
                    }
            }
        }
    }

    fun saveFoodEntry(imageUri: Uri) {
        viewModelScope.launch {
            val nutritionInfo = _uiState.value.nutritionInfo
            val selectedCandidate = _uiState.value.selectedCandidate
            if (nutritionInfo != null) {
                val dishName = selectedCandidate?.name
                saveFoodEntryUseCase(imageUri, nutritionInfo, dishName, null)
                _uiState.value = _uiState.value.copy(isSaved = true)
            }
        }
    }

    fun resetState() {
        _uiState.value = AnalysisUiState()
    }
}

