package com.caloriesresume.app.domain.usecase

import com.caloriesresume.app.domain.model.NutritionInfo
import com.caloriesresume.app.domain.repository.IFoodRepository
import javax.inject.Inject

class ConfirmDishUseCase @Inject constructor(
    private val repository: IFoodRepository
) {
    suspend operator fun invoke(imageId: Int, dishId: Int, foodItemPosition: Int, apiKey: String): Result<Pair<NutritionInfo, List<String>>> {
        return repository.confirmDishAndGetNutrition(imageId, dishId, foodItemPosition, apiKey)
    }
}

