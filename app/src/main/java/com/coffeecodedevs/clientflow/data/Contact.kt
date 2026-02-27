package com.coffeecodedevs.clientflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val company: String? = null,
    val phones: List<String>,
    val isClient: Boolean,
    val isEmployee: Boolean,
    val contact: String? = null,
    val orderName: String = "",
    val customerName: String = "",
    val orderAddress: String = "",
    val noteTitle: String = "",
    val reminderText: String = "",
    val reminderDate: String = "",
    val reminderTime: String = "",
    val isStandaloneNote: Boolean = false,
    val callLog: List<String> = emptyList()
)


class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }
}
