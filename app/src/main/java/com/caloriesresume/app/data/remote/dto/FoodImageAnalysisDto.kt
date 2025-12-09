package com.caloriesresume.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FoodImageAnalysisResponse(
    @SerializedName("foodFamily") val foodFamily: List<FoodFamilyItem>? = null,
    @SerializedName("foodType") val foodType: FoodTypeInfo? = null,
    @SerializedName("imageId") val imageId: Int? = null,
    @SerializedName("model_versions") val modelVersions: ModelVersions? = null,
    @SerializedName("occasion") val occasion: String? = null,
    @SerializedName("occasion_info") val occasionInfo: OccasionInfo? = null,
    @SerializedName("processed_image_size") val processedImageSize: ProcessedImageSize? = null,
    @SerializedName("segmentation_results") val segmentationResults: List<SegmentationResult>? = null
)

data class FoodFamilyItem(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("prob") val prob: Double? = null
)

data class FoodTypeInfo(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null
)

data class ModelVersions(
    @SerializedName("drinks") val drinks: String? = null,
    @SerializedName("foodType") val foodType: String? = null,
    @SerializedName("foodgroups") val foodgroups: String? = null,
    @SerializedName("foodrec") val foodrec: String? = null,
    @SerializedName("ingredients") val ingredients: String? = null,
    @SerializedName("ontology") val ontology: String? = null,
    @SerializedName("segmentation") val segmentation: String? = null
)

data class OccasionInfo(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("translation") val translation: String? = null
)

data class ProcessedImageSize(
    @SerializedName("height") val height: Int? = null,
    @SerializedName("width") val width: Int? = null
)

data class SegmentationResult(
    @SerializedName("center") val center: Center? = null,
    @SerializedName("contained_bbox") val containedBbox: BoundingBox? = null,
    @SerializedName("food_item_position") val foodItemPosition: Int? = null,
    @SerializedName("polygon") val polygon: List<Int>? = null,
    @SerializedName("recognition_results") val recognitionResults: List<RecognitionResult>? = null
)

data class Center(
    @SerializedName("x") val x: Int? = null,
    @SerializedName("y") val y: Int? = null
)

data class BoundingBox(
    @SerializedName("h") val h: Int? = null,
    @SerializedName("w") val w: Int? = null,
    @SerializedName("x") val x: Int? = null,
    @SerializedName("y") val y: Int? = null
)

data class RecognitionResult(
    @SerializedName("foodFamily") val foodFamily: List<FoodFamilyItem>? = null,
    @SerializedName("foodType") val foodType: FoodTypeInfo? = null,
    @SerializedName("hasNutriScore") val hasNutriScore: Boolean? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("nutri_score") val nutriScore: NutriScore? = null,
    @SerializedName("prob") val prob: Double? = null,
    @SerializedName("subclasses") val subclasses: List<RecognitionResult>? = null
)

data class NutriScore(
    @SerializedName("nutri_score_category") val nutriScoreCategory: String? = null,
    @SerializedName("nutri_score_standardized") val nutriScoreStandardized: Int? = null
)



