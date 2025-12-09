package com.caloriesresume.app.domain.usecase

import com.caloriesresume.app.data.local.database.entities.FoodEntry
import com.caloriesresume.app.domain.repository.IFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFoodEntriesByDateRangeUseCase @Inject constructor(
    private val repository: IFoodRepository
) {
    operator fun invoke(startTime: Long, endTime: Long): Flow<List<FoodEntry>> {
        return repository.getFoodEntriesByDateRange(startTime, endTime)
    }
}




