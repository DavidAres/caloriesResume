package com.caloriesresume.app.domain.model

data class DishCandidate(
    val dishId: Int,
    val name: String,
    val prob: Double,
    val nutriScore: LogMealNutriScore?,
    val foodItemPosition: Int,
    val boundingBox: BoundingBox?
)

data class SegmentationCandidate(
    val foodItemPosition: Int,
    val boundingBox: BoundingBox?,
    val candidates: List<DishCandidate>,
    val center: Center?
)

data class SegmentationResult(
    val imageId: Int,
    val processedImageSize: ImageSize?,
    val segments: List<SegmentationCandidate>
)

data class BoundingBox(
    val x: Int,
    val y: Int,
    val w: Int,
    val h: Int
)

data class Center(
    val x: Int,
    val y: Int
)

data class ImageSize(
    val width: Int,
    val height: Int
)

