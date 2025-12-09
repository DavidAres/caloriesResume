package com.caloriesresume.app.domain.usecase

import com.caloriesresume.app.domain.repository.IFoodRepository
import javax.inject.Inject

class GetDietaryAdviceUseCase @Inject constructor(
    private val repository: IFoodRepository
) {
    suspend operator fun invoke(nutritionSummary: String, apiKey: String): Result<String> {
        return repository.getDietaryAdvice(nutritionSummary, apiKey)
    }
}




