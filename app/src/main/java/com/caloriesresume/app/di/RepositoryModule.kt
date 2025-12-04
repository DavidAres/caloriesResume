package com.caloriesresume.app.di

import android.content.Context
import com.caloriesresume.app.data.local.database.dao.FoodEntryDao
import com.caloriesresume.app.data.remote.api.OpenAIApi
import com.caloriesresume.app.data.remote.api.FoodImageAnalysisApi
import com.caloriesresume.app.data.repository.FoodRepository
import com.caloriesresume.app.domain.repository.IFoodRepository
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindFoodRepository(
        foodRepository: FoodRepository
    ): IFoodRepository
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryProviderModule {
    @Provides
    @Singleton
    fun provideFoodRepository(
        @ApplicationContext context: Context,
        foodEntryDao: FoodEntryDao,
        foodImageAnalysisApi: FoodImageAnalysisApi,
        openAIApi: OpenAIApi,
        gson: Gson
    ): FoodRepository {
        return FoodRepository(foodEntryDao, foodImageAnalysisApi, openAIApi, context, gson)
    }
}

