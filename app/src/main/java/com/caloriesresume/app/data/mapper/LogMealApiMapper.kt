package com.caloriesresume.app.data.mapper

import com.caloriesresume.app.data.local.database.entities.NutritionData
import com.caloriesresume.app.data.remote.dto.*
import com.caloriesresume.app.domain.model.NutritionInfo
import com.google.gson.Gson

fun NutritionalInfoResponse.toNutritionInfo(): NutritionInfo? {
    android.util.Log.d("LogMealApiMapper", "Procesando NutritionalInfoResponse - nutritionalInfo: $nutritionalInfo")
    val nutritionalInfo = nutritionalInfo ?: return null
    
    val totalNutrients = nutritionalInfo.totalNutrients ?: return null
    
    // Extraer nutrientes usando los códigos de la API
    fun getNutrient(code: String): Double? {
        return totalNutrients[code]?.quantity
    }
    
    // Convertir unidades según sea necesario
    fun getNutrientInGrams(code: String): Double? {
        val nutrient = totalNutrients[code]
        return when (nutrient?.unit) {
            "g" -> nutrient.quantity
            "mg" -> nutrient.quantity?.div(1000.0)
            "µg", "mcg" -> nutrient.quantity?.div(1000000.0)
            else -> nutrient?.quantity
        }
    }
    
    fun getNutrientInMg(code: String): Double? {
        val nutrient = totalNutrients[code]
        return when (nutrient?.unit) {
            "mg" -> nutrient.quantity
            "g" -> nutrient.quantity?.times(1000.0)
            "µg", "mcg" -> nutrient.quantity?.div(1000.0)
            else -> nutrient?.quantity
        }
    }
    
    val calories = nutritionalInfo.calories ?: getNutrient("ENERC_KCAL") ?: 0.0
    val protein = getNutrientInGrams("PROCNT") ?: 0.0
    val carbohydrates = getNutrientInGrams("CHOCDF") ?: 0.0
    val fat = getNutrientInGrams("FAT") ?: 0.0
    val fiber = getNutrientInGrams("FIBTG")
    val sugar = getNutrientInGrams("SUGAR")
    val sodium = getNutrientInMg("NA")
    val calcium = getNutrientInMg("CA")
    val iron = getNutrientInMg("FE")
    val phosphorus = getNutrientInMg("P")
    val potassium = getNutrientInMg("K")
    val vitaminA = getNutrientInMg("VITA_RAE") // En µg RAE, convertimos a mg
    val vitaminC = getNutrientInMg("VITC")
    
    android.util.Log.d("LogMealApiMapper", "Nutrientes extraídos: calories=$calories, protein=$protein, carbs=$carbohydrates, fat=$fat")
    
    return NutritionInfo(
        calories = calories,
        protein = protein,
        carbohydrates = carbohydrates,
        fat = fat,
        fiber = fiber,
        sugar = sugar,
        sodium = sodium,
        calcium = calcium,
        iron = iron,
        phosphorus = phosphorus,
        potassium = potassium,
        vitaminA = vitaminA,
        vitaminC = vitaminC,
        ingredients = emptyList()
    )
}

fun NutritionalInfoResponse.toNutritionData(): NutritionData {
    val nutritionalInfo = nutritionalInfo ?: return NutritionData(
        calories = 0.0,
        protein = 0.0,
        carbohydrates = 0.0,
        fat = 0.0,
        fiber = null,
        sugar = null,
        sodium = null,
        calcium = null,
        iron = null,
        phosphorus = null,
        potassium = null,
        vitaminA = null,
        vitaminC = null,
        ingredients = emptyList()
    )
    
    val totalNutrients = nutritionalInfo.totalNutrients ?: return NutritionData(
        calories = nutritionalInfo.calories ?: 0.0,
        protein = 0.0,
        carbohydrates = 0.0,
        fat = 0.0,
        fiber = null,
        sugar = null,
        sodium = null,
        calcium = null,
        iron = null,
        phosphorus = null,
        potassium = null,
        vitaminA = null,
        vitaminC = null,
        ingredients = emptyList()
    )
    
    fun getNutrientInGrams(code: String): Double? {
        val nutrient = totalNutrients[code]
        return when (nutrient?.unit) {
            "g" -> nutrient.quantity
            "mg" -> nutrient.quantity?.div(1000.0)
            "µg", "mcg" -> nutrient.quantity?.div(1000000.0)
            else -> nutrient?.quantity
        }
    }
    
    fun getNutrientInMg(code: String): Double? {
        val nutrient = totalNutrients[code]
        return when (nutrient?.unit) {
            "mg" -> nutrient.quantity
            "g" -> nutrient.quantity?.times(1000.0)
            "µg", "mcg" -> nutrient.quantity?.div(1000.0)
            else -> nutrient?.quantity
        }
    }
    
    return NutritionData(
        calories = nutritionalInfo.calories ?: totalNutrients["ENERC_KCAL"]?.quantity ?: 0.0,
        protein = getNutrientInGrams("PROCNT") ?: 0.0,
        carbohydrates = getNutrientInGrams("CHOCDF") ?: 0.0,
        fat = getNutrientInGrams("FAT") ?: 0.0,
        fiber = getNutrientInGrams("FIBTG"),
        sugar = getNutrientInGrams("SUGAR"),
        sodium = getNutrientInMg("NA"),
        calcium = getNutrientInMg("CA"),
        iron = getNutrientInMg("FE"),
        phosphorus = getNutrientInMg("P"),
        potassium = getNutrientInMg("K"),
        vitaminA = getNutrientInMg("VITA_RAE"),
        vitaminC = getNutrientInMg("VITC"),
        ingredients = emptyList()
    )
}

fun IngredientsResponse.getIngredientNames(): List<String> {
    return ingredients?.mapNotNull { it.name } ?: emptyList()
}

