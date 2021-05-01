package com.example.snookerscore.database

import androidx.room.TypeConverter
import com.example.snookerscore.domain.DomainPlayerScore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class Converters {
    private var gson: Gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<DomainPlayerScore> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<DomainPlayerScore?>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<DomainPlayerScore?>?): String {
        return gson.toJson(someObjects)
    }

//    @TypeConverter
//    fun stringToPotStack(data: String?): ArrayDeque<Pot> {
//        if (data == null) {
//            return ArrayDeque()
//        }
//        val listType: Type = object : TypeToken<ArrayDeque<Pot>>() {}.type
//        return gson.fromJson(data, listType)
//    }
//
//    @TypeConverter
//    fun potStackToString(someObjects: ArrayDeque<Pot>): String {
//        return gson.toJson(someObjects)
//    }
//
//    @TypeConverter
//    fun sealedClassToString(sealedClass: Pot) : String = gson.toJson(sealedClass)
//
//    @TypeConverter
//    fun sealedClassFromString(sealedClass: String) : Pot = sealedClass.let { gson.fromJson(it) }
}