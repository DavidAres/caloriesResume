package com.caloriesresume.app.domain.usecase

import android.net.Uri
import com.caloriesresume.app.domain.model.NutritionInfo
import com.caloriesresume.app.domain.repository.IFoodRepository
import javax.inject.Inject

class SaveFoodEntryUseCase @Inject constructor(
    private val repository: IFoodRepository
) {
    suspend operator fun invoke(imageUri: Uri, nutritionInfo: NutritionInfo, dishName: String?, logMealResponse: com.caloriesresume.app.domain.model.LogMealResponse?): Long {
        return repository.saveFoodEntry(imageUri, nutritionInfo, dishName, logMealResponse)
    }
}

