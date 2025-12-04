package com.caloriesresume.app.data.local.database.converters

import androidx.room.TypeConverter
import com.caloriesresume.app.data.local.database.entities.NutritionData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NutritionConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromNutritionData(value: NutritionData): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toNutritionData(value: String): NutritionData {
        val type = object : TypeToken<NutritionData>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}

