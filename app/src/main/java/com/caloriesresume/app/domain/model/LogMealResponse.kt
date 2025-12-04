package com.caloriesresume.app.domain.model

data class LogMealResponse(
    val foodFamily: List<LogMealFoodFamily> = emptyList(),
    val foodType: LogMealFoodType? = null,
    val imageId: Int? = null,
    val occasion: String? = null,
    val occasionInfo: LogMealOccasionInfo? = null,
    val processedImageSize: LogMealImageSize? = null,
    val segmentationResults: List<LogMealSegmentationResult> = emptyList(),
    val recognitionResults: List<LogMealRecognitionResult> = emptyList()
) {
    val hasFoodInfo: Boolean
        get() = foodFamily.isNotEmpty() || 
                foodType != null || 
                recognitionResults.isNotEmpty() ||
                segmentationResults.isNotEmpty()
    
    val hasValidData: Boolean
        get() = hasFoodInfo && (recognitionResults.isNotEmpty() || segmentationResults.isNotEmpty())
    
    val primaryFoodName: String?
        get() = recognitionResults.firstOrNull()?.name ?: segmentationResults.firstOrNull()?.recognitionResults?.firstOrNull()?.name
    
    val allFoodNames: List<String>
        get() = (recognitionResults.mapNotNull { it.name } + 
                segmentationResults.flatMap { it.recognitionResults?.mapNotNull { it.name } ?: emptyList() }).distinct()
}

data class LogMealFoodFamily(
    val id: Int?,
    val name: String?,
    val prob: Double?
)

data class LogMealFoodType(
    val id: Int?,
    val name: String?
)

data class LogMealOccasionInfo(
    val id: Int?,
    val translation: String?
)

data class LogMealImageSize(
    val height: Int?,
    val width: Int?
)

data class LogMealSegmentationResult(
    val center: LogMealCenter?,
    val containedBbox: LogMealBoundingBox?,
    val foodItemPosition: Int?,
    val polygon: List<Int>?,
    val recognitionResults: List<LogMealRecognitionResult>?
)

data class LogMealCenter(
    val x: Int?,
    val y: Int?
)

data class LogMealBoundingBox(
    val h: Int?,
    val w: Int?,
    val x: Int?,
    val y: Int?
)

data class LogMealRecognitionResult(
    val foodFamily: List<LogMealFoodFamily>?,
    val foodType: LogMealFoodType?,
    val hasNutriScore: Boolean?,
    val id: Int?,
    val name: String?,
    val nutriScore: LogMealNutriScore?,
    val prob: Double?,
    val subclasses: List<LogMealRecognitionResult>?
)

data class LogMealNutriScore(
    val nutriScoreCategory: String?,
    val nutriScoreStandardized: Int?
)

