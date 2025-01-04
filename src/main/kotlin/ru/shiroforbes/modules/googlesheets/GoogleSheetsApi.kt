@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.modules.googlesheets

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.jetbrains.exposed.sql.exposedLogger
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

/**
 * Service providing connection to Google APIs.
 *
 * @property [serviceAccountKeyFile] path to file with service credentials (/service-account-key.json.json by default)
 * @property [scopes] the list of OAuth 2.0 scopes for use with API.
 */
class GoogleSheetsApiConnectionService(
    private val serviceAccountKeyFile: String = "/service-account-key.json",
    private val scopes: List<String>,
    applicationName: String = "Google API Service",
) {
    val service: Sheets

    init {
        val credentials =
            GoogleCredentials.fromStream(object {}.javaClass.classLoader.getResourceAsStream(serviceAccountKeyFile))
                .createScoped(listOf("https://www.googleapis.com/auth/spreadsheets"))

        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
        service = Sheets.Builder(httpTransport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName(applicationName)
            .build()
    }

}

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
    constructor(
        connectionService: GoogleSheetsApiConnectionService,
        spreadsheetId: String,
        clazz: KClass<T>,
        vararg dataRanges: String,
        defaultConversionClass: Class<*> = Class.forName("kotlin.text.StringsKt"),
    ) : this(connectionService, spreadsheetId, clazz, dataRanges.toList(), defaultConversionClass)

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

object Obj {
    @JvmStatic
    fun main(args: Array<String>) {
        val dataRanges =
            listOf(
                "Конд!C5:C33",
                "Конд!B5:B33",
                "Конд!D5:D33",
                "Конд!E5:E33",
                "Конд!L5:L33",
                "Конд!G5:G33",
                "Конд!I5:I33",
                "Конд!K5:K33",
            )
        GoogleSheetsService(
            GoogleSheetsApiConnectionService(
                "/googlesheets/service-account-key.json",
                listOf(SheetsScopes.SPREADSHEETS_READONLY),
            ),
            "19fm18aFwdENQHXRu3ekG1GRJtiIe-k1-XCMgtMQXFSQ",
            RatingRow::class,
            dataRanges,
        ).getWhileNotEmpty().map {
            println(it)
        }
    }
}
