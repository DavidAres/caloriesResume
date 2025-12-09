package com.caloriesresume.app.domain.usecase

import android.net.Uri
import com.caloriesresume.app.domain.repository.IFoodRepository
import javax.inject.Inject

class SegmentFoodImageUseCase @Inject constructor(
    private val repository: IFoodRepository
) {
    suspend operator fun invoke(imageUri: Uri, apiKey: String): Result<com.caloriesresume.app.domain.model.SegmentationResult> {
        return repository.segmentFoodImage(imageUri, apiKey)
    }
}



