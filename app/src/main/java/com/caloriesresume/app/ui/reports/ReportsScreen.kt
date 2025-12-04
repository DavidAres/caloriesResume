package com.caloriesresume.app.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.caloriesresume.app.domain.usecase.NutritionSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel(),
    onGetAdvice: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes Nutricionales") },
                actions = {
                    IconButton(
                        onClick = {
                            onGetAdvice(viewModel.getSummaryAsString())
                        }
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Consejos")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            PeriodSelector(
                selectedPeriod = uiState.period,
                onPeriodSelected = { period ->
                    viewModel.loadReport(period)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                NutritionSummaryCard(uiState.summary, uiState.period)
            }
        }
    }
}

@Composable
fun PeriodSelector(
    selectedPeriod: ReportPeriod,
    onPeriodSelected: (ReportPeriod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedPeriod == ReportPeriod.DAILY,
            onClick = { onPeriodSelected(ReportPeriod.DAILY) },
            label = { Text("Diario") },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = selectedPeriod == ReportPeriod.WEEKLY,
            onClick = { onPeriodSelected(ReportPeriod.WEEKLY) },
            label = { Text("Semanal") },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = selectedPeriod == ReportPeriod.MONTHLY,
            onClick = { onPeriodSelected(ReportPeriod.MONTHLY) },
            label = { Text("Mensual") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun NutritionSummaryCard(
    summary: NutritionSummary,
    period: ReportPeriod
) {
    val periodName = when (period) {
        ReportPeriod.DAILY -> "Diario"
        ReportPeriod.WEEKLY -> "Semanal"
        ReportPeriod.MONTHLY -> "Mensual"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Resumen $periodName",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Total de comidas: ${summary.entryCount}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            SummaryRow("Calorías", "${summary.calories.toInt()} kcal")
            SummaryRow("Proteínas", "${summary.protein.toInt()} g")
            SummaryRow("Carbohidratos", "${summary.carbohydrates.toInt()} g")
            SummaryRow("Grasas", "${summary.fat.toInt()} g")

            if (summary.fiber > 0) {
                SummaryRow("Fibra", "${summary.fiber.toInt()} g")
            }
            if (summary.sugar > 0) {
                SummaryRow("Azúcar", "${summary.sugar.toInt()} g")
            }
            if (summary.sodium > 0) {
                SummaryRow("Sodio", "${summary.sodium.toInt()} mg")
            }
            if (summary.calcium > 0) {
                SummaryRow("Calcio", "${summary.calcium.toInt()} mg")
            }
            if (summary.iron > 0) {
                SummaryRow("Hierro", "${summary.iron.toInt()} mg")
            }
            if (summary.phosphorus > 0) {
                SummaryRow("Fósforo", "${summary.phosphorus.toInt()} mg")
            }
            if (summary.potassium > 0) {
                SummaryRow("Potasio", "${summary.potassium.toInt()} mg")
            }
            if (summary.vitaminA > 0) {
                SummaryRow("Vitamina A", "${summary.vitaminA.toInt()} IU")
            }
            if (summary.vitaminC > 0) {
                SummaryRow("Vitamina C", "${summary.vitaminC.toInt()} mg")
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

