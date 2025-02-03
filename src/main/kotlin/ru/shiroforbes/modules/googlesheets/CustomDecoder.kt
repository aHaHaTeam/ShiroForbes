package ru.shiroforbes.modules.googlesheets

import ru.shiroforbes.model.Group
import java.util.*
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class CustomDecoder : Decoder {
    override fun supports(type: KType): Boolean =
        when (type.jvmErasure) {
            Float::class -> true
            Group::class -> true
            Boolean::class -> true
            else -> false
        }

    override fun convert(string: String, type: KType): Any? =
        when (type.jvmErasure) {
            Float::class -> toFloatOrNull(string)
            Group::class -> toGroupOrNull(string)
            Boolean::class -> toBooleanOrNull(string)
            else -> throw IllegalArgumentException("Unsupported type $type")
        }

    private fun toFloatOrNull(string: String): Float? =
        try {
            string
                .filter { !it.isWhitespace() }
                .split(',')
                .joinToString(".")
                .toFloat()
        } catch (e: Exception) {
            null
        }

    private fun toGroupOrNull(string: String): Group? = Group.entries.find { it.text == string }

    private fun toBooleanOrNull(string: String): Boolean? =
        when (string.lowercase(Locale.getDefault())) {
            "1" -> true
            "0" -> false
            "true" -> true
            "false" -> false
            else -> null
        }
}