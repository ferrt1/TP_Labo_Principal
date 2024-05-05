package com.example.cypher_vault.database

import androidx.room.TypeConverter
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceLandmark
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun faceContourListFromString(value: String): List<FaceContour> {
        val listType = object : TypeToken<List<FaceContour>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun faceContourListToString(list: List<FaceContour>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun faceLandmarkListFromString(value: String): List<FaceLandmark> {
        val listType = object : TypeToken<List<FaceLandmark>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun faceLandmarkListToString(list: List<FaceLandmark>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}

