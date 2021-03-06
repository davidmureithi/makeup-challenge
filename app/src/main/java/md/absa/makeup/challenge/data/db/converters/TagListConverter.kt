package md.absa.makeup.challenge.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TagListConverter {

    @TypeConverter
    fun toString(productColors: ArrayList<String>): String {
        val gson = Gson()
        return gson.toJson(productColors)
    }

    @TypeConverter
    fun fromString(string: String): ArrayList<String> {
        val listType: Type = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(string, listType)
    }
}
