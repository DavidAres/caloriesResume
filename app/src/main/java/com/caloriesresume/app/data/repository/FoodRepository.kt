package com.caloriesresume.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.caloriesresume.app.data.local.database.dao.FoodEntryDao
import com.caloriesresume.app.data.local.database.entities.FoodEntry
import com.caloriesresume.app.data.mapper.toLogMealResponse
import com.caloriesresume.app.data.mapper.toNutritionData
import com.caloriesresume.app.data.mapper.toNutritionInfo
import com.caloriesresume.app.data.mapper.toNutritionInfo as nutritionalInfoResponseToNutritionInfo
import com.caloriesresume.app.data.mapper.getIngredientNames
import com.caloriesresume.app.data.mapper.toSegmentationResult
import com.caloriesresume.app.domain.model.LogMealResponse
import com.caloriesresume.app.data.remote.api.FoodImageAnalysisApi
import com.caloriesresume.app.data.remote.dto.*
import com.caloriesresume.app.data.remote.api.OpenAIApi
import com.caloriesresume.app.data.remote.dto.Message
import com.caloriesresume.app.data.remote.dto.OpenAIRequest
import com.caloriesresume.app.domain.model.NutritionInfo
import com.caloriesresume.app.domain.repository.IFoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val foodEntryDao: FoodEntryDao,
    private val foodImageAnalysisApi: FoodImageAnalysisApi,
    private val openAIApi: OpenAIApi,
    private val context: Context,
    private val gson: Gson
) : IFoodRepository {
    override fun getAllFoodEntries(): Flow<List<FoodEntry>> = foodEntryDao.getAllFoodEntries()

    override fun getFoodEntryById(id: Long): Flow<FoodEntry?> = 
        foodEntryDao.getAllFoodEntries().map { entries -> entries.find { it.id == id } }

    override fun getFoodEntriesByDateRange(startTime: Long, endTime: Long): Flow<List<FoodEntry>> =
        foodEntryDao.getFoodEntriesByDateRange(startTime, endTime)

    override fun getFoodEntriesByDate(date: Long): Flow<List<FoodEntry>> =
        foodEntryDao.getFoodEntriesByDate(date)

    override suspend fun segmentFoodImage(imageUri: Uri, apiKey: String): Result<com.caloriesresume.app.domain.model.SegmentationResult> {
        return withContext(Dispatchers.IO) {
            try {
                val imageFile = compressImage(uriToFile(imageUri))
                val requestFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

                val segmentationResponse = foodImageAnalysisApi.analyzeFoodImage(
                    apiKey = "Bearer $apiKey",
                    image = imagePart
                )

                if (!segmentationResponse.isSuccessful || segmentationResponse.body() == null) {
                    val errorBody = segmentationResponse.errorBody()?.string() ?: "Sin detalles"
                    android.util.Log.e("FoodRepository", "Error en segmentación: ${segmentationResponse.code()} - $errorBody")
                    return@withContext Result.failure(Exception("Error al analizar la imagen: ${segmentationResponse.code()}"))
                }

                val responseBody: ResponseBody = segmentationResponse.body() ?: return@withContext Result.failure(Exception("Respuesta vacía de la API"))
                val rawJson = responseBody.string()
                android.util.Log.d("FoodRepository", "Respuesta segmentación: $rawJson")
                
                val segmentationDto = gson.fromJson(rawJson, FoodImageAnalysisResponse::class.java)
                val segmentationResult = segmentationDto.toSegmentationResult()
                
                if (segmentationResult == null || segmentationResult.segments.isEmpty()) {
                    return@withContext Result.failure(Exception("No se detectaron platos en la imagen"))
                }
                
                Result.success(segmentationResult)
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("Permission", ignoreCase = true) == true -> 
                        "Error de permisos al acceder a la imagen"
                    e.message?.contains("FileNotFoundException", ignoreCase = true) == true -> 
                        "No se pudo encontrar el archivo de imagen"
                    e.message?.contains("IOException", ignoreCase = true) == true -> 
                        "Error de lectura/escritura: ${e.message}"
                    e.message?.contains("SecurityException", ignoreCase = true) == true -> 
                        "Error de seguridad al acceder a la imagen"
                    else -> "Error al procesar la imagen: ${e.message ?: e.javaClass.simpleName}"
                }
                Result.failure(Exception(errorMessage, e))
            }
        }
    }
    
    override suspend fun confirmDishAndGetNutrition(imageId: Int, dishId: Int, foodItemPosition: Int, apiKey: String): Result<Pair<NutritionInfo, List<String>>> {
        return withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("FoodRepository", "Confirmando plato - imageId: $imageId, dishId: $dishId, foodItemPosition: $foodItemPosition")
                
                if (imageId <= 0) {
                    return@withContext Result.failure(Exception("imageId inválido: $imageId"))
                }
                if (dishId <= 0) {
                    return@withContext Result.failure(Exception("dishId inválido: $dishId"))
                }
                if (foodItemPosition <= 0) {
                    return@withContext Result.failure(Exception("foodItemPosition inválido: $foodItemPosition"))
                }
                
                val confirmRequest = DishConfirmationRequest(
                    imageId = imageId,
                    confirmedClass = listOf(dishId),
                    source = listOf("logmeal"),
                    foodItemPosition = listOf(foodItemPosition)
                )

                android.util.Log.d("FoodRepository", "Request de confirmación: ${com.google.gson.Gson().toJson(confirmRequest)}")

                val confirmResponse = foodImageAnalysisApi.confirmDish(
                    apiKey = "Bearer $apiKey",
                    request = confirmRequest
                )

                if (!confirmResponse.isSuccessful || confirmResponse.body() == null) {
                    val errorBody = confirmResponse.errorBody()?.string() ?: "Sin detalles"
                    android.util.Log.e("FoodRepository", "Error al confirmar plato: ${confirmResponse.code()} - $errorBody")
                    return@withContext Result.failure(Exception("Error al confirmar el plato: ${confirmResponse.code()}"))
                }

                android.util.Log.d("FoodRepository", "Plato confirmado exitosamente")
                
                // Pequeño delay para asegurar que la confirmación se procese completamente
                kotlinx.coroutines.delay(500)

                // Según la documentación de LogMeal, primero obtenemos los ingredientes
                val ingredientsResponse = foodImageAnalysisApi.getIngredients(
                    apiKey = "Bearer $apiKey",
                    request = IngredientsRequest(imageId = imageId)
                )

                val ingredients = if (ingredientsResponse.isSuccessful && ingredientsResponse.body() != null) {
                    ingredientsResponse.body()!!.getIngredientNames()
                } else {
                    val errorBody = ingredientsResponse.errorBody()?.string() ?: "Sin detalles"
                    android.util.Log.w("FoodRepository", "Advertencia al obtener ingredientes: ${ingredientsResponse.code()} - $errorBody")
                    emptyList()
                }

                android.util.Log.d("FoodRepository", "Ingredientes obtenidos: ${ingredients.size}")

                // Luego obtenemos la información nutricional
                android.util.Log.d("FoodRepository", "Solicitando información nutricional para imageId: $imageId")
                val requestBody = NutritionalInfoRequest(imageId = imageId)
                android.util.Log.d("FoodRepository", "Request body: ${gson.toJson(requestBody)}")
                
                val nutritionalInfoResponse = foodImageAnalysisApi.getNutritionalInfo(
                    apiKey = "Bearer $apiKey",
                    request = requestBody
                )

                if (!nutritionalInfoResponse.isSuccessful) {
                    val errorBody = nutritionalInfoResponse.errorBody()?.string() ?: "Sin detalles"
                    android.util.Log.e("FoodRepository", "Error al obtener información nutricional: ${nutritionalInfoResponse.code()} - $errorBody")
                    return@withContext Result.failure(Exception("Error al obtener información nutricional: ${nutritionalInfoResponse.code()} - $errorBody"))
                }
                
                if (nutritionalInfoResponse.body() == null) {
                    android.util.Log.e("FoodRepository", "Respuesta vacía de información nutricional")
                    return@withContext Result.failure(Exception("Respuesta vacía de información nutricional"))
                }

                val nutritionalInfo = nutritionalInfoResponse.body() ?: return@withContext Result.failure(Exception("Respuesta vacía de información nutricional"))
                
                // Log de la respuesta completa para debugging
                val responseJson = gson.toJson(nutritionalInfo)
                android.util.Log.d("FoodRepository", "Respuesta nutricional completa: $responseJson")
                android.util.Log.d("FoodRepository", "nutritionalInfo: ${nutritionalInfo.nutritionalInfo}")
                android.util.Log.d("FoodRepository", "nutritionalInfo.calories: ${nutritionalInfo.nutritionalInfo?.calories}")
                android.util.Log.d("FoodRepository", "nutritionalInfo.totalNutrients: ${nutritionalInfo.nutritionalInfo?.totalNutrients}")
                
                val nutritionInfo = nutritionalInfo.nutritionalInfoResponseToNutritionInfo()

                if (nutritionInfo == null) {
                    android.util.Log.e("FoodRepository", "No se pudo procesar la información nutricional - nutrition es null o macros es null")
                    return@withContext Result.failure(Exception("No se pudo procesar la información nutricional. Verifica que el plato haya sido confirmado correctamente."))
                }

                android.util.Log.d("FoodRepository", "Información nutricional obtenida: ${nutritionInfo.calories} kcal")

                val nutritionInfoWithIngredients = NutritionInfo(
                    calories = nutritionInfo.calories,
                    protein = nutritionInfo.protein,
                    carbohydrates = nutritionInfo.carbohydrates,
                    fat = nutritionInfo.fat,
                    fiber = nutritionInfo.fiber,
                    sugar = nutritionInfo.sugar,
                    sodium = nutritionInfo.sodium,
                    calcium = nutritionInfo.calcium,
                    iron = nutritionInfo.iron,
                    phosphorus = nutritionInfo.phosphorus,
                    potassium = nutritionInfo.potassium,
                    vitaminA = nutritionInfo.vitaminA,
                    vitaminC = nutritionInfo.vitaminC,
                    ingredients = ingredients
                )

                Result.success(Pair(nutritionInfoWithIngredients, ingredients))
            } catch (e: Exception) {
                Result.failure(Exception("Error al confirmar plato y obtener información: ${e.message}", e))
            }
        }
    }
    

    override suspend fun saveFoodEntry(imageUri: Uri, nutritionInfo: NutritionInfo, dishName: String?, logMealResponse: LogMealResponse?): Long {
        return withContext(Dispatchers.IO) {
            val nutritionData = nutritionInfo.toNutritionData()
            
            val foodGroupsJson = logMealResponse?.let {
                if (it.foodFamily.isNotEmpty()) {
                    gson.toJson(it.foodFamily.mapNotNull { family -> family.name })
                } else null
            }
            
            val recipesJson = logMealResponse?.let {
                if (it.recognitionResults.isNotEmpty()) {
                    gson.toJson(it.recognitionResults.mapNotNull { rec -> rec.name })
                } else null
            }
            
            val foodEntry = FoodEntry(
                imageUri = imageUri.toString(),
                nutritionData = nutritionData,
                dishName = dishName,
                foodType = logMealResponse?.foodType?.name,
                foodGroups = foodGroupsJson,
                recipes = recipesJson,
                mealEstimation = null
            )
            foodEntryDao.insertFoodEntry(foodEntry)
        }
    }

    override suspend fun deleteFoodEntry(id: Long) {
        withContext(Dispatchers.IO) {
            foodEntryDao.deleteFoodEntry(id)
        }
    }

    override suspend fun getDietaryAdvice(
        nutritionSummary: String,
        apiKey: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Eres un nutricionista experto. Analiza detalladamente la siguiente información 
                    nutricional semanal de un usuario obtenida mediante análisis de imágenes de 
                    comida con LogMeal API y genera una dieta completa para la siguiente semana 
                    (7 días) basándote EXCLUSIVAMENTE en estos datos para equilibrar los déficits 
                    nutricionales detectados.
                    
                    Información nutricional semanal:
                    $nutritionSummary
                    
                    PROCESO DE ANÁLISIS OBLIGATORIO:
                    1. Analiza los datos nutricionales semanales proporcionados
                    2. Identifica los déficits nutricionales (nutrientes por debajo de las 
                       recomendaciones diarias/semanales)
                    3. Identifica los excesos nutricionales (nutrientes por encima de las 
                       recomendaciones)
                    4. Diseña una dieta que corrija específicamente estos desequilibrios
                    5. Prioriza alimentos ricos en los nutrientes deficitarios
                    6. Reduce o equilibra los nutrientes en exceso
                    
                    REQUISITOS OBLIGATORIOS PARA LA DIETA:
                    - La dieta DEBE estar basada en los datos nutricionales semanales proporcionados
                    - DEBE corregir los déficits identificados en los datos
                    - DEBE equilibrar los excesos detectados
                    - TODOS los días de la semana (Lunes a Domingo) DEBEN tener desayuno, comida y cena
                    - NO puedes omitir ninguna comida en ningún día
                    - TODOS los platos deben ser DIFERENTES entre días (máxima variedad)
                    - No repitas el mismo plato en diferentes días de la semana
                    - Varía los tipos de proteínas, carbohidratos y verduras a lo largo de la semana
                    - Selecciona ingredientes específicos que aporten los nutrientes deficitarios
                    
                    Para cada día de la semana (Lunes a Domingo), debes especificar OBLIGATORIAMENTE:
                    1. DESAYUNO: Nombre del plato diferente a los demás días, ingredientes con 
                       cantidades exactas (en gramos, unidades o medidas caseras como tazas, 
                       cucharadas, etc.) que contribuyan a equilibrar los déficits nutricionales
                    2. COMIDA: Nombre del plato diferente a los demás días, ingredientes con 
                       cantidades exactas que contribuyan a equilibrar los déficits nutricionales
                    3. CENA: Nombre del plato diferente a los demás días, ingredientes con 
                       cantidades exactas que contribuyan a equilibrar los déficits nutricionales
                    
                    Formato de respuesta:
                    - Usa un formato claro y estructurado día por día
                    - Para cada plato, lista todos los ingredientes con sus cantidades específicas
                    - Las cantidades deben ser precisas y prácticas (ej: "200g de pollo", 
                      "1 taza de arroz", "2 huevos", "100g de brócoli")
                    - Incluye una breve descripción de cómo preparar cada plato si es relevante
                    - Asegúrate de que la dieta sea variada, equilibrada, sin repeticiones y 
                      específicamente diseñada para corregir los déficits nutricionales identificados
                    
                    Considera que esta información proviene de análisis de imágenes de comida 
                    reales, por lo que los valores pueden tener variaciones. La dieta debe 
                    estar en español, ser fácil de seguir y estar directamente relacionada con 
                    los datos nutricionales semanales proporcionados para equilibrar los déficits.
                    
                    IMPORTANTE: Debes proporcionar TODA la dieta completa en una sola respuesta. 
                    NO digas que continuarás en la siguiente respuesta ni que la información 
                    está incompleta. Completa todos los 7 días con todas las comidas en esta respuesta.
                """.trimIndent()

                val request = OpenAIRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(
                        Message(role = "system", content = "Eres un nutricionista experto y amigable especializado en crear dietas personalizadas completas con recetas detalladas e ingredientes específicos."),
                        Message(role = "user", content = prompt)
                    ),
                    maxTokens = 4000
                )

                val response = openAIApi.getChatCompletion(
                    authorization = "Bearer $apiKey",
                    request = request
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body() ?: return@withContext Result.failure(Exception("Respuesta vacía de la API"))
                    val choice = body.choices?.firstOrNull()
                    
                    if (choice?.finishReason == "length") {
                        return@withContext Result.failure(Exception("La respuesta fue truncada. Por favor, intenta con un resumen más corto o contacta al soporte."))
                    }
                    
                    val advice = choice?.message?.content
                        ?: "No se pudo generar consejo dietético."
                    Result.success(advice)
                } else {
                    Result.failure(Exception("Error en la API: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private suspend fun uriToFile(uri: Uri): File {
        return withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("No se pudo abrir el stream de la imagen")
            
            val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            
            inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            if (!tempFile.exists() || tempFile.length() == 0L) {
                throw Exception("No se pudo crear el archivo temporal de la imagen")
            }
            
            tempFile
        }
    }
    
    private suspend fun compressImage(imageFile: File): File {
        return withContext(Dispatchers.IO) {
            val maxSizeBytes = 1048576L // 1MB
            val maxDimension = 1920 // Máxima dimensión en píxeles
            
            if (!imageFile.exists() || !imageFile.canRead()) {
                return@withContext imageFile
            }
            
            val fileSize = imageFile.length()
            if (fileSize <= 0) {
                return@withContext imageFile
            }
            
            if (fileSize <= maxSizeBytes) {
                return@withContext imageFile
            }
            
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(imageFile.absolutePath, options)
            
            if (options.outWidth <= 0 || options.outHeight <= 0) {
                return@withContext imageFile
            }
            
            val scale = calculateInSampleSize(options, maxDimension)
            
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
            }
            
            var bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, decodeOptions)
                ?: return@withContext imageFile
            
            var quality = 85
            var compressedFile = File(context.cacheDir, "compressed_image_${System.currentTimeMillis()}.jpg")
            var fileSizeCheck = Long.MAX_VALUE
            
            while (fileSizeCheck > maxSizeBytes && quality > 20) {
                if (compressedFile.exists()) {
                    compressedFile.delete()
                }
                compressedFile = File(context.cacheDir, "compressed_image_${System.currentTimeMillis()}.jpg")
                
                FileOutputStream(compressedFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                }
                
                fileSizeCheck = compressedFile.length()
                
                if (fileSizeCheck > maxSizeBytes && quality > 20) {
                    quality -= 10
                    bitmap.recycle()
                    bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, decodeOptions)
                        ?: break
                }
            }
            
            bitmap.recycle()
            
            if (compressedFile.exists() && compressedFile.length() > 0) {
                imageFile.delete()
                compressedFile
            } else {
                imageFile
            }
        }
    }
    
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqSize: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        
        if (height > reqSize || width > reqSize) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while ((halfHeight / inSampleSize) >= reqSize && (halfWidth / inSampleSize) >= reqSize) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
}

