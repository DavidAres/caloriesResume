package com.caloriesresume.app.ui.history

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.caloriesresume.app.data.local.database.entities.FoodEntry
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodEntryDetailScreen(
    entry: FoodEntry,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(entry.timestamp))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(entry.dishName ?: "Detalles de la Comida") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(Uri.parse(entry.imageUri))
                    .build(),
                contentDescription = "Imagen de comida",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Fecha y Hora",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                dateString,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Información Nutricional",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            NutritionRow("Calorías", "${entry.nutritionData.calories.toInt()} kcal")
            NutritionRow("Proteínas", "${entry.nutritionData.protein.toInt()} g")
            NutritionRow("Carbohidratos", "${entry.nutritionData.carbohydrates.toInt()} g")
            NutritionRow("Grasas", "${entry.nutritionData.fat.toInt()} g")

            if (entry.nutritionData.fiber != null && entry.nutritionData.fiber!! > 0) {
                NutritionRow("Fibra", "${entry.nutritionData.fiber!!.toInt()} g")
            }
            if (entry.nutritionData.sugar != null && entry.nutritionData.sugar!! > 0) {
                NutritionRow("Azúcar", "${entry.nutritionData.sugar!!.toInt()} g")
            }
            if (entry.nutritionData.sodium != null && entry.nutritionData.sodium!! > 0) {
                NutritionRow("Sodio", "${entry.nutritionData.sodium!!.toInt()} mg")
            }
            if (entry.nutritionData.calcium != null && entry.nutritionData.calcium!! > 0) {
                NutritionRow("Calcio", "${entry.nutritionData.calcium!!.toInt()} mg")
            }
            if (entry.nutritionData.iron != null && entry.nutritionData.iron!! > 0) {
                NutritionRow("Hierro", "${entry.nutritionData.iron!!.toInt()} mg")
            }
            if (entry.nutritionData.phosphorus != null && entry.nutritionData.phosphorus!! > 0) {
                NutritionRow("Fósforo", "${entry.nutritionData.phosphorus!!.toInt()} mg")
            }
            if (entry.nutritionData.potassium != null && entry.nutritionData.potassium!! > 0) {
                NutritionRow("Potasio", "${entry.nutritionData.potassium!!.toInt()} mg")
            }
            if (entry.nutritionData.vitaminA != null && entry.nutritionData.vitaminA!! > 0) {
                NutritionRow("Vitamina A", "${entry.nutritionData.vitaminA!!.toInt()} IU")
            }
            if (entry.nutritionData.vitaminC != null && entry.nutritionData.vitaminC!! > 0) {
                NutritionRow("Vitamina C", "${entry.nutritionData.vitaminC!!.toInt()} mg")
            }

            if (entry.nutritionData.ingredients.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Ingredientes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                entry.nutritionData.ingredients.forEach { ingredient ->
                    Text(
                        "• $ingredient",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            if (entry.foodType != null) {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Tipo de Comida",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    entry.foodType,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (entry.foodGroups != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Grupos Alimentarios",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    entry.foodGroups,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (entry.recipes != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Recetas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    entry.recipes,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (entry.mealEstimation != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Estimación de Comida",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    entry.mealEstimation,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun NutritionRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

