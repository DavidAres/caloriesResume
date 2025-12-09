package com.caloriesresume.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object TestUtils {
    fun <T> Flow<T>.skipItems(count: Int): Flow<T> = this
}




