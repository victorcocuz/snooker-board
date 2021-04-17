package com.example.snookerscore.database

import androidx.room.TypeConverter
import com.example.snookerscore.fragments.game.FrameScore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class Converters {
    var gson: Gson = Gson()

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
}