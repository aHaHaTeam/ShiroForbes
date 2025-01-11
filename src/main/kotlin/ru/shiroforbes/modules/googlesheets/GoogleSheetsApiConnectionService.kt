package ru.shiroforbes.modules.googlesheets

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials

/**
 * Service providing connection to Google APIs.
 *
 * @property [serviceAccountKeyFile] path to file with service credentials (/service-account-key.json.json by default)
 * @property [scopes] the list of OAuth 2.0 scopes for use with API.
 */
class GoogleSheetsApiConnectionService(
    private val serviceAccountKeyFile: String,
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