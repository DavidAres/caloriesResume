package com.caloriesresume.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DishConfirmationRequest(
    @SerializedName("imageId") val imageId: Int,
    @SerializedName("confirmedClass") val confirmedClass: List<Int>,
    @SerializedName("source") val source: List<String>,
    @SerializedName("food_item_position") val foodItemPosition: List<Int>
)

data class DishConfirmationResponse(
    @SerializedName("code") val code: Int? = null,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("message") val message: String? = null
)

data class NutritionalInfoRequest(
    @SerializedName("imageId") val imageId: Int
)

data class NutritionalInfoResponse(
    @SerializedName("foodName") val foodName: List<String>? = null,
    @SerializedName("hasNutritionalInfo") val hasNutritionalInfo: Boolean? = null,
    @SerializedName("ids") val ids: List<Int>? = null,
    @SerializedName("imageId") val imageId: Int? = null,
    @SerializedName("image_nutri_score") val imageNutriScore: NutriScoreDto? = null,
    @SerializedName("nutritional_info") val nutritionalInfo: NutritionalInfoDto? = null,
    @SerializedName("nutritional_info_per_item") val nutritionalInfoPerItem: List<NutritionalInfoPerItemDto>? = null,
    @SerializedName("serving_size") val servingSize: Double? = null
)

data class NutriScoreDto(
    @SerializedName("nutri_score_category") val nutriScoreCategory: String? = null,
    @SerializedName("nutri_score_standardized") val nutriScoreStandardized: Int? = null
)

data class NutritionalInfoDto(
    @SerializedName("calories") val calories: Double? = null,
    @SerializedName("dailyIntakeReference") val dailyIntakeReference: Map<String, DailyIntakeReferenceDto>? = null,
    @SerializedName("totalNutrients") val totalNutrients: Map<String, NutrientDto>? = null
)

data class DailyIntakeReferenceDto(
    @SerializedName("label") val label: String? = null,
    @SerializedName("level") val level: String? = null,
    @SerializedName("percent") val percent: Double? = null
)

data class NutrientDto(
    @SerializedName("label") val label: String? = null,
    @SerializedName("quantity") val quantity: Double? = null,
    @SerializedName("unit") val unit: String? = null
)

data class NutritionalInfoPerItemDto(
    @SerializedName("food_item_position") val foodItemPosition: Int? = null,
    @SerializedName("hasNutriScore") val hasNutriScore: Boolean? = null,
    @SerializedName("hasNutritionalInfo") val hasNutritionalInfo: Boolean? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nutri_score") val nutriScore: NutriScoreDto? = null,
    @SerializedName("nutritional_info") val nutritionalInfo: NutritionalInfoDto? = null,
    @SerializedName("serving_size") val servingSize: Double? = null
)

data class IngredientsRequest(
    @SerializedName("imageId") val imageId: Int
)

data class IngredientsResponse(
    @SerializedName("imageId") val imageId: Int? = null,
    @SerializedName("ingredients") val ingredients: List<IngredientDto>? = null
)

data class IngredientDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("quantity") val quantity: QuantityDto? = null
)

data class QuantityDto(
    @SerializedName("value") val value: Double? = null,
    @SerializedName("unit") val unit: String? = null
)

