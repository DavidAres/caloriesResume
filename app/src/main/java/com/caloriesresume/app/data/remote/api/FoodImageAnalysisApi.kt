package com.caloriesresume.app.data.remote.api

import com.caloriesresume.app.data.remote.dto.DishConfirmationRequest
import com.caloriesresume.app.data.remote.dto.DishConfirmationResponse
import com.caloriesresume.app.data.remote.dto.IngredientsRequest
import com.caloriesresume.app.data.remote.dto.IngredientsResponse
import com.caloriesresume.app.data.remote.dto.NutritionalInfoRequest
import com.caloriesresume.app.data.remote.dto.NutritionalInfoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface FoodImageAnalysisApi {
    @Multipart
    @POST("v2/image/segmentation/complete")
    suspend fun analyzeFoodImage(
        @Header("Authorization") apiKey: String,
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>
    
    @POST("v2/image/confirm/dish")
    suspend fun confirmDish(
        @Header("Authorization") apiKey: String,
        @Body request: DishConfirmationRequest
    ): Response<DishConfirmationResponse>
    
    @POST("v2/nutrition/recipe/nutritionalInfo")
    suspend fun getNutritionalInfo(
        @Header("Authorization") apiKey: String,
        @Body request: NutritionalInfoRequest
    ): Response<NutritionalInfoResponse>
    
    @POST("v2/nutrition/recipe/ingredients")
    suspend fun getIngredients(
        @Header("Authorization") apiKey: String,
        @Body request: IngredientsRequest
    ): Response<IngredientsResponse>
}

