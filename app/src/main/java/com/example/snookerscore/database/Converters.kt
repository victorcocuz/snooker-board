package com.example.snookerscore.database

import androidx.room.TypeConverter
import com.example.snookerscore.fragments.game.FrameScore
import com.example.snookerscore.fragments.game.Pot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class Converters {
    private var gson: Gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<FrameScore> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<FrameScore?>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<FrameScore?>?): String {
        return gson.toJson(someObjects)
    }

//    @TypeConverter
//    fun stringToBallStack(data: String?): ArrayDeque<Ball> {
//        if (data == null) {
//            return ArrayDeque()
//        }
//        val listType: Type = object : TypeToken<ArrayDeque<Ball>>() {}.type
//        return gson.fromJson(data, listType)
//    }
//
//    @TypeConverter
//    fun ballStackToString(someObjects: ArrayDeque<Ball>): String {
//        return gson.toJson(someObjects)
//    }
//
//    @TypeConverter
//    fun sealedClassToString(sealedClass: Pot) : String = gson.toJson(sealedClass)
//
//    @TypeConverter
//    fun sealedClassFromString(sealedClass: String) : Pot = sealedClass.let { gson.fromJson(it) }
}