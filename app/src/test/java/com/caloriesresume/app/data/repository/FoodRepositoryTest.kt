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

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = FoodRepository(foodEntryDao, foodImageAnalysisApi, openAIApi, context)
    }

    @Test
    fun `getAllFoodEntries should return flow from dao`() = runTest {
        val entries = listOf(
            FoodEntry(
                id = 1L,
                imageUri = "content://test",
                nutritionData = NutritionData(calories = 500.0, protein = 25.0, carbohydrates = 50.0, fat = 20.0)
            )
        )

        whenever(foodEntryDao.getAllFoodEntries())
            .thenReturn(flowOf(entries))

        repository.getAllFoodEntries().collect { result ->
            assertEquals(entries, result)
        }
    }

    @Test
    fun `saveFoodEntry should call dao insert`() = runTest {
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

        val result = repository.saveFoodEntry(imageUri, nutritionInfo)

        assertEquals(expectedId, result)
        verify(foodEntryDao).insertFoodEntry(any())
    }

    @Test
    fun `deleteFoodEntry should call dao delete`() = runTest {
        val id = 1L

        repository.deleteFoodEntry(id)

        verify(foodEntryDao).deleteFoodEntry(id)
    }

    @Test
    fun `analyzeFoodImage should return success when API succeeds`() = runTest {
        val imageUri = Uri.parse("content://test")
        val apiKey = "test-api-key"
        
        val tempFile = File.createTempFile("test", ".jpg")
        tempFile.deleteOnExit()
        
        whenever(context.cacheDir).thenReturn(tempFile.parentFile)
        whenever(context.contentResolver.openInputStream(imageUri))
            .thenReturn(java.io.FileInputStream(tempFile))

        val nutrition = FoodNutritionInfo(
            calories = 500.0,
            protein = 25.0,
            carbs = 50.0,
            fat = 20.0
        )
        val response = FoodImageAnalysisResponse(
            food = FoodInfo(
                name = "Test Food",
                nutrition = nutrition,
                ingredients = listOf(Ingredient(name = "Test Ingredient"))
            )
        )

        whenever(foodImageAnalysisApi.analyzeFoodImage(any(), any()))
            .thenReturn(Response.success(response))

        val result = repository.analyzeFoodImage(imageUri, apiKey)

        assertTrue(result.isSuccess)
        val nutritionInfo = result.getOrNull()
        assertNotNull(nutritionInfo)
        assertEquals(500.0, nutritionInfo?.calories, 0.01)
        assertEquals(25.0, nutritionInfo?.protein, 0.01)
    }

    @Test
    fun `analyzeFoodImage should return failure when API fails`() = runTest {
        val imageUri = Uri.parse("content://test")
        val apiKey = "test-api-key"
        
        val tempFile = File.createTempFile("test", ".jpg")
        tempFile.deleteOnExit()
        
        whenever(context.cacheDir).thenReturn(tempFile.parentFile)
        whenever(context.contentResolver.openInputStream(imageUri))
            .thenReturn(java.io.FileInputStream(tempFile))

        whenever(foodImageAnalysisApi.analyzeFoodImage(any(), any()))
            .thenReturn(Response.error(400, "Error".toResponseBody("text/plain".toMediaType())))

        val result = repository.analyzeFoodImage(imageUri, apiKey)

        assertTrue(result.isFailure)
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

        whenever(openAIApi.getChatCompletion(any(), any()))
            .thenReturn(Response.success(response))

        val result = repository.getDietaryAdvice(summary, apiKey)

        assertTrue(result.isSuccess)
        assertEquals(expectedAdvice, result.getOrNull())
    }

    @Test
    fun `getDietaryAdvice should return failure when API fails`() = runTest {
        val summary = "Test summary"
        val apiKey = "test-api-key"

        whenever(openAIApi.getChatCompletion(any(), any()))
            .thenReturn(Response.error(400, "Error".toResponseBody("text/plain".toMediaType())))

        val result = repository.getDietaryAdvice(summary, apiKey)

        assertTrue(result.isFailure)
    }
}

