package ru.shiroforbes.modules.googlesheets

import com.google.api.services.sheets.v4.SheetsScopes
import ru.shiroforbes.config.GoogleSheetsConfig

class RatingDeserializer(
    config: GoogleSheetsConfig,
) {
    private val urbanDeserializer =
        GoogleSheetsService(
            GoogleSheetsApiConnectionService(
                config.credentialsPath,
                listOf(SheetsScopes.SPREADSHEETS_READONLY),
            ),
            config.ratingSpreadsheetId,
            RatingRow::class,
            config.urbanRatingRanges,
        )
    private val countrysideDeserializer =
        GoogleSheetsService(
            GoogleSheetsApiConnectionService(
                config.credentialsPath,
                listOf(SheetsScopes.SPREADSHEETS_READONLY),
            ),
            config.ratingSpreadsheetId,
            RatingRow::class,
            config.countrysideRatingRanges,
        )

    fun getCountrysideRating(): List<RatingRow> = countrysideDeserializer.getWhileNotEmpty()

    fun getUrbanRating(): List<RatingRow> = urbanDeserializer.getWhileNotEmpty()

    fun getAllRatings(): List<RatingRow> = countrysideDeserializer.getWhileNotEmpty() + urbanDeserializer.getWhileNotEmpty()
}
