package ru.shiroforbes.modules.googlesheets

import org.jetbrains.exposed.sql.exposedLogger
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

class ReflectiveTableParser<T : Any>(
    private val clazz: KClass<T>,
    private val decoders: List<Decoder>,
) : TableParser<T> {
    override fun parse(table: List<List<String>>): List<T> {
        if (table.isEmpty()) {
            return listOf()
        }

        return table.map {
            val args =
                it.mapIndexed { index, value ->
                    val converted =
                        try {
                            convert(
                                value,
                                clazz.primaryConstructor!!.parameters[index].type,
                            )
                        } catch (e: Exception) {
                            null
                        }
                    if (converted == null) {
                        exposedLogger.debug(
                            "Cannot convert \"{}\" to \"{}\"",
                            value,
                            clazz.primaryConstructor!!.parameters[index].type,
                        )
                    }
                    return@mapIndexed converted
                }
            try {
                clazz.primaryConstructor!!.call(*(args.toTypedArray()))
            } catch (e: Exception) {
                exposedLogger.error(
                    "Cannot convert \"${
                        args.toTypedArray().joinToString(", ")
                    }\" to \"${clazz.simpleName}\"",
                )
                throw e
            }
        }
    }

    private fun convert(
        str: String,
        type: KType,
    ): Any? {
        if (type.jvmErasure.simpleName == String::class.simpleName) {
            return str
        }
        for (decoder in decoders) {
            if (decoder.supports(type)) {
                return decoder.convert(str, type)
            }
        }
        throw IllegalArgumentException("Cannot find decoder to convert \"$str\" to \"${type.jvmErasure.simpleName}\"")
    }
}
