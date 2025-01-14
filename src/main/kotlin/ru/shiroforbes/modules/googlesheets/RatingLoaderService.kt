package ru.shiroforbes.modules.googlesheets

import com.google.api.services.sheets.v4.SheetsScopes
import ru.shiroforbes.config.GoogleSheetsConfig

class RatingLoaderService(
    config: GoogleSheetsConfig,
) {
    private val urbanDeserializer = googleSheetsService(config, config.urbanRatingRanges)
    private val urbanDeserializerSemester2 = googleSheetsService(config, config.urbanRatingRangesSemester2)
    private val countrysideDeserializer = googleSheetsService(config, config.countrysideRatingRanges)
    private val countrysideDeserializerSemester2 = googleSheetsService(config, config.countrysideRatingRangesSemester2)

    private fun googleSheetsService(config: GoogleSheetsConfig, ranges: List<String>): GoogleSheetsService<RatingRow> =
        GoogleSheetsService(
            GoogleSheetsApiConnectionService(
                config.credentialsPath,
                listOf(SheetsScopes.SPREADSHEETS_READONLY),
            ),
            config.ratingSpreadsheetId,
            RatingRow::class,
            ranges,
            Class.forName("ru.shiroforbes.database.InitKt"),
        )

    fun getCountrysideRating(): List<RatingRow> = countrysideDeserializer.getWhileNotEmpty()
    fun getCountrysideRatingSemester2(): List<RatingRow> = countrysideDeserializerSemester2.getWhileNotEmpty()

    fun getUrbanRating(): List<RatingRow> = urbanDeserializer.getWhileNotEmpty()
    fun getUrbanRatingSemester2(): List<RatingRow> = urbanDeserializerSemester2.getWhileNotEmpty()
}
