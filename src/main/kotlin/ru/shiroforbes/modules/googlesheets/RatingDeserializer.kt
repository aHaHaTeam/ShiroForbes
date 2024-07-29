@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.modules.googlesheets

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import io.ktor.util.reflect.*
import java.io.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

/**
 * Service providing connection to Google APIs.
 *
 * @property [credentialsFilePath] path to file with credentials (/credentials.json by default)
 * @property [scopes] the list of OAuth 2.0 scopes for use with API.
 */
class GoogleSheetsApiConnectionService(
    val credentialsFilePath: String = "/credentials.json",
    val scopes: List<String>,
    applicationName: String = "Google API Service",
    tokensDirectoryPath: String = "tokens",
) {
    val service: Sheets

    init {
        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
        service =
            Sheets
                .Builder(httpTransport, jsonFactory, getCredentials(jsonFactory, httpTransport, tokensDirectoryPath))
                .setApplicationName(applicationName)
                .build()
    }

    /**
     * Creates an authorized Credential object.
     */
    @Throws(IOException::class)
    private fun getCredentials(
        jsonFactory: JsonFactory,
        httpTransport: NetHttpTransport,
        tokensDirectoryPath: String,
    ): Credential {
        // Load client secrets.
        val inputStream =
            GoogleSheetsService::class.java.getResourceAsStream(credentialsFilePath)
                ?: throw FileNotFoundException("Resource not found: " + credentialsFilePath)
        val clientSecrets: GoogleClientSecrets =
            GoogleClientSecrets.load(jsonFactory, InputStreamReader(inputStream))

        // Build flow and trigger user authorization request.
        val flow: GoogleAuthorizationCodeFlow =
            GoogleAuthorizationCodeFlow
                .Builder(
                    httpTransport,
                    jsonFactory,
                    clientSecrets,
                    scopes,
                ).setDataStoreFactory(FileDataStoreFactory(File(tokensDirectoryPath)))
                .setAccessType("offline")
                .build()
        val receiver: LocalServerReceiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    /**
     * Build a new authorized API client service.
     */
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
    private vararg val dataRanges: String,
) {
    fun getRating(): List<T> {
        val response =
            connectionService
                .service
                .spreadsheets()
                .values()
                .batchGet(spreadsheetId)
                .apply {
                    ranges = dataRanges.toList()
                }.execute()
        val valueRanges = response.valueRanges
        if (valueRanges.isEmpty()) {
            println("No data found.")
            return listOf()
        }
        val maxLength = valueRanges.maxOf { it.getValues().size }
        val argLists = MutableList<MutableList<Any?>>(maxLength) { mutableListOf() }
        for (range in valueRanges) {
            for (rowIndexed in range.getValues().withIndex()) {
                argLists[rowIndexed.index].addAll(rowIndexed.value)
            }
        }
        return argLists.map {
            val args =
                it.mapIndexed { index, value ->
                    convert(
                        value as? String,
                        clazz.primaryConstructor!!.parameters[index].type,
                    )
                }
            clazz.primaryConstructor!!.call(*(args.toTypedArray()))
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
        val conversionClass = Class.forName("kotlin.text.StringsKt")

        val typeName = type.jvmErasure.simpleName
        val conversionFuncName = "to${typeName}OrNull"

        val conversionFunc =
            try {
                conversionClass.getMethod(conversionFuncName, String::class.java)
            } catch (e: NoSuchMethodException) {
                throw IllegalArgumentException("Type $type is not a valid string conversion target")
            }

        conversionFunc.isAccessible = true
        return conversionFunc.invoke(null, str)
            ?: throw IllegalArgumentException("'$str' cannot be parsed to type $type")
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
                "Конд!G5:G33",
                "Конд!I5:I33",
                "Конд!K5:K33",
            )
        GoogleSheetsService(
            GoogleSheetsApiConnectionService(
                "/googlesheets/credentials.json",
                listOf(SheetsScopes.SPREADSHEETS_READONLY),
            ),
            "spreadsheetId",
            RatingRow::class,
            *dataRanges.toTypedArray(),
        ).getRating().map {
            println(it)
        }
    }
}
