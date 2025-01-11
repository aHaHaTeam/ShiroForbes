@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.modules.googlesheets

import org.jetbrains.exposed.sql.exposedLogger
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

/**
 * Service for extracting data from Google spreadsheets
 *
 * @property [spreadsheetId] the id of the table
 * (e.g. spreadsheet [https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit](https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit)
 * has id "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms")
 *
 * Ranges must be represented in [A1 or R1C1 notation](https://developers.google.com/sheets/api/guides/concepts#cell).
 *
 */
class GoogleSheetsService<T : Any>(
    private val connectionService: GoogleSheetsApiConnectionService,
    private val spreadsheetId: String,
    private val clazz: KClass<T>,
    private val dataRanges: List<String>,
    private val defaultConversionClass: Class<*> = Class.forName("kotlin.text.StringsKt"),
) {
    fun getWhileNotEmpty(): List<T> {
        val response =
            connectionService
                .service
                .spreadsheets()
                .values()
                .batchGet(spreadsheetId)
                .apply {
                    ranges = dataRanges
                }.execute()
        val valueRanges = response.valueRanges
        if (valueRanges.isEmpty()) {
            return listOf()
        }

        val maxLength =
            valueRanges.maxOf { range ->
                if (range.isEmpty()) {
                    1000
                } else {
                    // if list is empty we ignore it
                    range
                        .getValues()
                        ?.takeWhile { row -> row.any { it.toString() != "" } && row.size > 0 }
                        ?.size ?: 0
                }
            }
        valueRanges.forEachIndexed { index, value ->
            if (value.getValues() == null) {
                valueRanges[index].setValues(MutableList<MutableList<Any>>(maxLength) { mutableListOf("0") })
            }
        }
        val argLists = MutableList<MutableList<Any?>>(maxLength) { mutableListOf() }
        for (range in valueRanges) {
            // println(range.range)
            for (rowIndexed in range.getValues().withIndex()) {
                if (rowIndexed.index >= maxLength) {
                    break
                }
                argLists[rowIndexed.index].addAll(rowIndexed.value)
            }
        }
        return argLists.map {
            val args =
                it.mapIndexed { index, value ->
                    val converted =
                        convert(
                            value as? String,
                            clazz.primaryConstructor!!.parameters[index].type,
                        )
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

    /**
     * Converts string [str] to value of the type [type]
     */
    private fun convert(
        str: String?,
        type: KType,
    ): Any? {
        if (str == null) {
            return null
        }
        if (type.jvmErasure.simpleName == String::class.simpleName) {
            return str
        }
        val typeName = type.jvmErasure.simpleName
        val conversionFuncName = "to${typeName}OrNull"

        for (conversionClass in listOf(defaultConversionClass, Class.forName("kotlin.text.StringsKt"))) {
            val conversionFunc =
                try {
                    conversionClass.getMethod(conversionFuncName, String::class.java)
                } catch (e: NoSuchMethodException) {
                    continue
                }

            conversionFunc.isAccessible = true
            val res = conversionFunc.invoke(null, str)
            if (res != null) {
                return res
            }
        }
        return null
    }
}
