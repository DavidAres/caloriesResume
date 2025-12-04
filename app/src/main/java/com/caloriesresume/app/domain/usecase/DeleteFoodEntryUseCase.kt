package com.caloriesresume.app.domain.usecase

import com.caloriesresume.app.domain.repository.IFoodRepository
import javax.inject.Inject

class DeleteFoodEntryUseCase @Inject constructor(
    private val repository: IFoodRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteFoodEntry(id)
    }
}


