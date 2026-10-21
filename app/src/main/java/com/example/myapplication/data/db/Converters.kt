package com.example.myapplication.data.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromListIntToString(list: List<Int>?): String {
        return list?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun fromStringToListInt(value: String?): List<Int> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    @TypeConverter
    fun fromListStringToString(list: List<String>?): String {
        return list?.joinToString(";") ?: ""
    }

    @TypeConverter
    fun fromStringToListString(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(";").map { it.trim() }
    }
}
