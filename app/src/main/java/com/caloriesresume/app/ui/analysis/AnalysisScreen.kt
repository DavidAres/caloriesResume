package com.caloriesresume.app.ui.analysis

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.caloriesresume.app.domain.model.NutritionInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    imageUri: Uri,
    foodApiKey: String,
    onSave: () -> Unit,
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(imageUri) {
        viewModel.resetState()
        viewModel.segmentImage(imageUri, foodApiKey)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Análisis Nutricional") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(imageUri)
                        .build()
                ),
                contentDescription = "Imagen de comida",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading && uiState.segmentationResult == null -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Analizando imagen...")
                }
                uiState.error != null -> {
                    val errorMessage = uiState.error
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Error al analizar la imagen",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                errorMessage ?: "Error desconocido",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.segmentImage(imageUri, foodApiKey)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                uiState.segmentationResult != null && uiState.selectedCandidate == null -> {
                    DishCandidatesSection(
                        segmentationResult = uiState.segmentationResult!!,
                        imageUri = imageUri,
                        onCandidateSelected = { candidate ->
                            viewModel.selectCandidate(candidate, foodApiKey)
                        }
                    )
                }
                uiState.isLoading && uiState.selectedCandidate != null -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Obteniendo información nutricional...")
                }
                uiState.nutritionInfo != null -> {
                    val nutritionInfo = uiState.nutritionInfo
                    if (nutritionInfo != null) {
                        NutritionInfoCard(nutritionInfo)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                    
                        if (!uiState.isSaved) {
                            Button(
                                onClick = {
                                    viewModel.saveFoodEntry(imageUri)
                                    onSave()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardar")
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Guardado exitosamente")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionInfoCard(nutritionInfo: NutritionInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Información Nutricional",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            NutritionRow("Calorías", "${nutritionInfo.calories.toInt()} kcal")
            NutritionRow("Proteínas", "${nutritionInfo.protein.toInt()} g")
            NutritionRow("Carbohidratos", "${nutritionInfo.carbohydrates.toInt()} g")
            NutritionRow("Grasas", "${nutritionInfo.fat.toInt()} g")
            
            nutritionInfo.fiber?.let {
                NutritionRow("Fibra", "${it.toInt()} g")
            }
            nutritionInfo.sugar?.let {
                NutritionRow("Azúcar", "${it.toInt()} g")
            }
            nutritionInfo.sodium?.let {
                NutritionRow("Sodio", "${it.toInt()} mg")
            }
            nutritionInfo.calcium?.let {
                NutritionRow("Calcio", "${it.toInt()} mg")
            }
            nutritionInfo.iron?.let {
                NutritionRow("Hierro", "${it.toInt()} mg")
            }
            nutritionInfo.phosphorus?.let {
                NutritionRow("Fósforo", "${it.toInt()} mg")
            }
            nutritionInfo.potassium?.let {
                NutritionRow("Potasio", "${it.toInt()} mg")
            }
            nutritionInfo.vitaminA?.let {
                NutritionRow("Vitamina A", "${it.toInt()} IU")
            }
            nutritionInfo.vitaminC?.let {
                NutritionRow("Vitamina C", "${it.toInt()} mg")
            }
            
            if (nutritionInfo.ingredients.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Ingredientes identificados:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                nutritionInfo.ingredients.forEach { ingredient ->
                    Text("• $ingredient")
                }
            }
        }
    }
}

@Composable
fun NutritionRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RawJsonCard(rawJson: String) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Respuesta JSON de LogMeal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = { expanded = !expanded }
                ) {
                    Text(if (expanded) "Ocultar" else "Mostrar")
                }
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    val scrollState = rememberScrollState()
                    Text(
                        text = rawJson,
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(scrollState),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }
    }
}

