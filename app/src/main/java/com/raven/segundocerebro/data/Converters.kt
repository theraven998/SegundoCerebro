package com.raven.segundocerebro.data

import androidx.room.TypeConverter

// Separador de unidad (U+001F): no aparece en texto escrito por el usuario.
private const val SEP = ""

class Converters {
    @TypeConverter
    fun fromParaType(value: ParaType): String = value.name

    @TypeConverter
    fun toParaType(value: String): ParaType =
        runCatching { ParaType.valueOf(value) }.getOrDefault(ParaType.INBOX)

    @TypeConverter
    fun fromTags(tags: List<String>): String =
        tags.filter { it.isNotBlank() }.joinToString(SEP)

    @TypeConverter
    fun toTags(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split(SEP)
}
