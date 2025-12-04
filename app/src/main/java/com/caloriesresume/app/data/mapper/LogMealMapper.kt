package com.caloriesresume.app.data.mapper

import com.caloriesresume.app.data.local.database.entities.NutritionData
import com.caloriesresume.app.data.remote.dto.*
import com.caloriesresume.app.domain.model.*
import com.google.gson.Gson

fun FoodImageAnalysisResponse.toLogMealResponse(): LogMealResponse {
    val foodFamily = foodFamily?.map {
        LogMealFoodFamily(
            id = it.id,
            name = it.name,
            prob = it.prob
        )
    } ?: emptyList()
    
    val foodType = foodType?.let {
        LogMealFoodType(
            id = it.id,
            name = it.name
        )
    }
    
    val occasionInfo = occasionInfo?.let {
        LogMealOccasionInfo(
            id = it.id,
            translation = it.translation
        )
    }
    
    val processedImageSize = processedImageSize?.let {
        LogMealImageSize(
            height = it.height,
            width = it.width
        )
    }
    
    val segmentationResults = segmentationResults?.map { seg ->
        LogMealSegmentationResult(
            center = seg.center?.let { 
                LogMealCenter(x = it.x, y = it.y) 
            },
            containedBbox = seg.containedBbox?.let {
                LogMealBoundingBox(
                    h = it.h,
                    w = it.w,
                    x = it.x,
                    y = it.y
                )
            },
            foodItemPosition = seg.foodItemPosition,
            polygon = seg.polygon,
            recognitionResults = seg.recognitionResults?.map { rec ->
                rec.toLogMealRecognitionResult()
            }
        )
    } ?: emptyList()
    
    val allRecognitionResults = segmentationResults.flatMap { seg ->
        seg.recognitionResults ?: emptyList()
    }
    
    return LogMealResponse(
        foodFamily = foodFamily,
        foodType = foodType,
        imageId = imageId,
        occasion = occasion,
        occasionInfo = occasionInfo,
        processedImageSize = processedImageSize,
        segmentationResults = segmentationResults,
        recognitionResults = allRecognitionResults
    )
}

fun RecognitionResult.toLogMealRecognitionResult(): LogMealRecognitionResult {
    return LogMealRecognitionResult(
        foodFamily = foodFamily?.map {
            LogMealFoodFamily(id = it.id, name = it.name, prob = it.prob)
        },
        foodType = foodType?.let {
            LogMealFoodType(id = it.id, name = it.name)
        },
        hasNutriScore = hasNutriScore,
        id = id,
        name = name,
        nutriScore = nutriScore?.let {
            LogMealNutriScore(
                nutriScoreCategory = it.nutriScoreCategory,
                nutriScoreStandardized = it.nutriScoreStandardized
            )
        },
        prob = prob,
        subclasses = subclasses?.map { it.toLogMealRecognitionResult() }
    )
}

fun LogMealResponse.toNutritionInfo(): NutritionInfo? {
    if (!hasValidData) {
        return null
    }
    
    val primaryResult = recognitionResults.firstOrNull()
    val foodName = primaryResult?.name ?: primaryFoodName ?: "Comida no identificada"
    
    return NutritionInfo(
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
        ingredients = allFoodNames
    )
}

fun LogMealResponse.toFoodEntryData(gson: Gson): Triple<NutritionData, String?, String?> {
    val nutritionData = NutritionData(
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
        ingredients = allFoodNames
    )
    
    val foodGroupsJson = if (foodFamily.isNotEmpty()) {
        gson.toJson(foodFamily.mapNotNull { it.name })
    } else null
    
    val recipesJson = if (recognitionResults.isNotEmpty()) {
        gson.toJson(recognitionResults.mapNotNull { it.name })
    } else null
    
    return Triple(nutritionData, foodGroupsJson, recipesJson)
}

fun FoodImageAnalysisResponse.toSegmentationResult(): com.caloriesresume.app.domain.model.SegmentationResult? {
    val imageId = imageId ?: return null
    android.util.Log.d("LogMealMapper", "Mapeando segmentación - imageId: $imageId")
    
    val segments = segmentationResults?.mapNotNull { seg ->
        val foodItemPosition = seg.foodItemPosition ?: return@mapNotNull null
        val recognitionResults = seg.recognitionResults ?: return@mapNotNull null
        
        android.util.Log.d("LogMealMapper", "Procesando segmento - foodItemPosition: $foodItemPosition, recognitionResults count: ${recognitionResults.size}")
        
        val candidates = recognitionResults
            .filter { it.id != null && it.name != null && it.prob != null }
            .sortedByDescending { it.prob }
            .take(5)
            .map { rec ->
                android.util.Log.d("LogMealMapper", "Creando candidato - id: ${rec.id}, name: ${rec.name}, prob: ${rec.prob}, foodItemPosition: $foodItemPosition")
                com.caloriesresume.app.domain.model.DishCandidate(
                    dishId = rec.id!!,
                    name = rec.name!!,
                    prob = rec.prob!!,
                    nutriScore = rec.nutriScore?.let {
                        LogMealNutriScore(
                            nutriScoreCategory = it.nutriScoreCategory,
                            nutriScoreStandardized = it.nutriScoreStandardized
                        )
                    },
                    foodItemPosition = foodItemPosition,
                    boundingBox = seg.containedBbox?.let {
                        com.caloriesresume.app.domain.model.BoundingBox(
                            x = it.x ?: 0,
                            y = it.y ?: 0,
                            w = it.w ?: 0,
                            h = it.h ?: 0
                        )
                    }
                )
            }
        
        if (candidates.isEmpty()) return@mapNotNull null
        
        com.caloriesresume.app.domain.model.SegmentationCandidate(
            foodItemPosition = foodItemPosition,
            boundingBox = seg.containedBbox?.let {
                com.caloriesresume.app.domain.model.BoundingBox(
                    x = it.x ?: 0,
                    y = it.y ?: 0,
                    w = it.w ?: 0,
                    h = it.h ?: 0
                )
            },
            candidates = candidates,
            center = seg.center?.let {
                com.caloriesresume.app.domain.model.Center(
                    x = it.x ?: 0,
                    y = it.y ?: 0
                )
            }
        )
    } ?: emptyList()
    
    val imageSize = processedImageSize?.let {
        com.caloriesresume.app.domain.model.ImageSize(
            width = it.width ?: 0,
            height = it.height ?: 0
        )
    }
    
    return com.caloriesresume.app.domain.model.SegmentationResult(
        imageId = imageId,
        processedImageSize = imageSize,
        segments = segments
    )
}

