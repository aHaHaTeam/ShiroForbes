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
import java.io.*

private object GoogleSheetsApiService {
    private const val APPLICATION_NAME = "Google Sheets API Java Quickstart"
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    private const val TOKENS_DIRECTORY_PATH = "tokens"

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES = listOf(SheetsScopes.SPREADSHEETS_READONLY)
    private const val CREDENTIALS_FILE_PATH = "/googlesheets/credentials.json"

    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
    private fun getCredentials(httpTransport: NetHttpTransport): Credential {
        // Load client secrets.
        val inputStream =
            RatingDeserializer::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
                ?: throw FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH)
        val clientSecrets: GoogleClientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))

        // Build flow and trigger user authorization request.
        val flow: GoogleAuthorizationCodeFlow =
            GoogleAuthorizationCodeFlow
                .Builder(
                    httpTransport,
                    JSON_FACTORY,
                    clientSecrets,
                    SCOPES,
                ).setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build()
        val receiver: LocalServerReceiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    /**
     * Build a new authorized API client service.
     */
    private val HTTP_TRANSPORT: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val service: Sheets =
        Sheets
            .Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build()
}

/**
 * Service for extracting rating from Google spreadsheets
 *
 * @property [spreadsheetId] the id of the table
 * (e.g. spreadsheet [https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit](https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit)
 * has id "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms")
 *
 * Ranges must be represented in [A1 or R1C1 notation](https://developers.google.com/sheets/api/guides/concepts#cell).
 *
 */
class RatingDeserializer(
    private val spreadsheetId: String,
    private val firstNameRange: String,
    private val lastNameRange: String,
    private val solvedProblemsRange: String,
    private val ratingRange: String,
    private val algebraPercentageRange: String,
    private val combinatoricsPercentageRange: String,
    private val geometryPercentageRange: String,
) {
    fun getRating(): List<RatingRow> {
        val response =
            GoogleSheetsApiService.service
                .spreadsheets()
                .values()
                .batchGet(spreadsheetId)
                .apply {
                    ranges = (
                        listOf(
                            firstNameRange,
                            lastNameRange,
                            solvedProblemsRange,
                            ratingRange,
                            algebraPercentageRange,
                            combinatoricsPercentageRange,
                            geometryPercentageRange,
                        )
                    )
                }.execute()
        val valueRanges = response.valueRanges
        if (valueRanges.isEmpty()) {
            println("No data found.")
        } else {
            val maxLength = valueRanges.maxOf { it.getValues().size }
            return List<RatingRow>(maxLength) {
                RatingRow(
                    valueRanges[0]
                        .getValues()[it]
                        .first()
                        .toString()
                        .trim(),
                    valueRanges[1]
                        .getValues()[it]
                        .first()
                        .toString()
                        .trim(),
                    valueRanges[2]
                        .getValues()[it]
                        .first()
                        .toString()
                        .toInt(),
                    valueRanges[3]
                        .getValues()[it]
                        .first()
                        .toString()
                        .toInt(),
                    valueRanges[4]
                        .getValues()[it]
                        .first()
                        .toString()
                        .toInt(),
                    valueRanges[5]
                        .getValues()[it]
                        .first()
                        .toString()
                        .toInt(),
                    valueRanges[6]
                        .getValues()[it]
                        .first()
                        .toString()
                        .toInt(),
                )
            }
        }
        return listOf()
    }
}

object Obj {
    @JvmStatic
    fun main(args: Array<String>) {
        RatingDeserializer(
            "spreadsheetId",
            "Конд!C5:C33",
            "Конд!B5:B33",
            "Конд!D5:D33",
            "Конд!E5:E33",
            "Конд!G5:G33",
            "Конд!I5:I33",
            "Конд!K5:K33",
        ).getRating().map {
            println(it)
        }
    }
}
