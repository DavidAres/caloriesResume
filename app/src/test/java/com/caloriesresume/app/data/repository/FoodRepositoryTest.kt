package com.caloriesresume.app.data.repository

import android.content.Context
import android.net.Uri
import com.caloriesresume.app.data.local.database.dao.FoodEntryDao
import com.caloriesresume.app.data.local.database.entities.FoodEntry
import com.caloriesresume.app.data.local.database.entities.NutritionData
import com.caloriesresume.app.data.remote.api.OpenAIApi
import com.caloriesresume.app.data.remote.api.FoodImageAnalysisApi
import com.caloriesresume.app.data.remote.dto.*
import com.caloriesresume.app.domain.model.NutritionInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class FoodRepositoryTest {

    @Mock
    private lateinit var foodEntryDao: FoodEntryDao

    @Mock
    private lateinit var foodImageAnalysisApi: FoodImageAnalysisApi

    @Mock
    private lateinit var openAIApi: OpenAIApi

    @Mock
    private lateinit var context: Context

    private lateinit var repository: FoodRepository
    private val gson = com.google.gson.Gson()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = FoodRepository(foodEntryDao, foodImageAnalysisApi, openAIApi, context, gson)
    }

    @Test
    fun `getAllFoodEntries should return flow from dao`() = runTest {
        val entries = listOf(
            FoodEntry(
                id = 1L,
                imageUri = "content://test",
                nutritionData = NutritionData(
                    calories = 500.0,
                    protein = 25.0,
                    carbohydrates = 50.0,
                    fat = 20.0,
                    fiber = null,
                    sugar = null,
                    sodium = null,
                    calcium = null,
                    iron = null,
                    phosphorus = null,
                    potassium = null,
                    vitaminA = null,
                    vitaminC = null
                )
            )
        )

        whenever(foodEntryDao.getAllFoodEntries())
            .thenReturn(flowOf(entries))

        repository.getAllFoodEntries().collect { result ->
            assertEquals(entries, result)
        }
    }


    @Test
    fun `deleteFoodEntry should call dao delete`() = runTest {
        val id = 1L

        repository.deleteFoodEntry(id)

        verify(foodEntryDao).deleteFoodEntry(id)
    }

    @Test
    fun `saveFoodEntry should call dao insert with correct parameters`() = runTest {
        val imageUri = Uri.parse("content://test")
        val nutritionInfo = NutritionInfo(
            calories = 500.0,
            protein = 25.0,
            carbohydrates = 50.0,
            fat = 20.0
        )
        val expectedId = 1L

        whenever(foodEntryDao.insertFoodEntry(any()))
            .thenReturn(expectedId)

        val result = repository.saveFoodEntry(imageUri, nutritionInfo, null, null)

        assertEquals(expectedId, result)
        verify(foodEntryDao).insertFoodEntry(any())
    }

    @Test
    fun `getDietaryAdvice should return success when API succeeds`() = runTest {
        val summary = "Test summary"
        val apiKey = "test-api-key"
        val expectedAdvice = "Eat more vegetables"

        val response = OpenAIResponse(
            id = "test-id",
            choices = listOf(
                Choice(
                    message = Message("assistant", expectedAdvice),
                    finishReason = "stop"
                )
            ),
            usage = Usage(10, 20, 30)
        )

        whenever(openAIApi.getChatCompletion(any(), any(), any()))
            .thenReturn(Response.success(response))

        val result = repository.getDietaryAdvice(summary, apiKey)

        assertTrue(result.isSuccess)
        assertEquals(expectedAdvice, result.getOrNull())
    }

    @Test
    fun `getDietaryAdvice should return failure when API fails`() = runTest {
        val summary = "Test summary"
        val apiKey = "test-api-key"

        whenever(openAIApi.getChatCompletion(any(), any(), any()))
            .thenReturn(Response.error(400, "Error".toResponseBody("text/plain".toMediaType())))

        val result = repository.getDietaryAdvice(summary, apiKey)

        assertTrue(result.isFailure)
    }
}

