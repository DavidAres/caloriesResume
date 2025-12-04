package com.caloriesresume.app.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caloriesresume.app.domain.usecase.CalculateNutritionSummaryUseCase
import com.caloriesresume.app.domain.usecase.GetFoodEntriesByDateRangeUseCase
import com.caloriesresume.app.domain.usecase.NutritionSummary as UseCaseNutritionSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ReportPeriod {
    DAILY, WEEKLY, MONTHLY
}

data class ReportsUiState(
    val period: ReportPeriod = ReportPeriod.DAILY,
    val summary: UseCaseNutritionSummary = UseCaseNutritionSummary(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getFoodEntriesByDateRangeUseCase: GetFoodEntriesByDateRangeUseCase,
    private val calculateNutritionSummaryUseCase: CalculateNutritionSummaryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState

    init {
        loadReport(ReportPeriod.DAILY)
    }

    fun loadReport(period: ReportPeriod) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, period = period)
            
            val now = System.currentTimeMillis()
            val startTime = when (period) {
                ReportPeriod.DAILY -> {
                    val calendar = java.util.Calendar.getInstance()
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    calendar.set(java.util.Calendar.MINUTE, 0)
                    calendar.set(java.util.Calendar.SECOND, 0)
                    calendar.set(java.util.Calendar.MILLISECOND, 0)
                    calendar.timeInMillis
                }
                ReportPeriod.WEEKLY -> {
                    val calendar = java.util.Calendar.getInstance()
                    calendar.add(java.util.Calendar.DAY_OF_YEAR, -7)
                    calendar.timeInMillis
                }
                ReportPeriod.MONTHLY -> {
                    val calendar = java.util.Calendar.getInstance()
                    calendar.add(java.util.Calendar.MONTH, -1)
                    calendar.timeInMillis
                }
            }

            getFoodEntriesByDateRangeUseCase(startTime, now).collect { entries ->
                val summary = calculateNutritionSummaryUseCase(entries)
                _uiState.value = _uiState.value.copy(
                    summary = summary,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun getSummaryAsString(): String {
        val summary = _uiState.value.summary
        val periodName = when (_uiState.value.period) {
            ReportPeriod.DAILY -> "Diario"
            ReportPeriod.WEEKLY -> "Semanal"
            ReportPeriod.MONTHLY -> "Mensual"
        }
        
        return """
            Resumen Nutricional ($periodName):
            - Total de comidas: ${summary.entryCount}
            - Calorías: ${summary.calories.toInt()} kcal
            - Proteínas: ${summary.protein.toInt()} g
            - Carbohidratos: ${summary.carbohydrates.toInt()} g
            - Grasas: ${summary.fat.toInt()} g
            - Fibra: ${summary.fiber.toInt()} g
            - Calcio: ${summary.calcium.toInt()} mg
            - Hierro: ${summary.iron.toInt()} mg
            - Vitamina C: ${summary.vitaminC.toInt()} mg
        """.trimIndent()
    }
}

