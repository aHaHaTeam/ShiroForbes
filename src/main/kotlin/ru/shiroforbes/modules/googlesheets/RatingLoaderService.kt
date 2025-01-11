package ru.shiroforbes.modules.googlesheets

import com.google.api.services.sheets.v4.SheetsScopes
import ru.shiroforbes.config.GoogleSheetsConfig
import ru.shiroforbes.config.RatingRanges
import ru.shiroforbes.database.toFloatOrNull

class RatingLoaderService(
    private val config: GoogleSheetsConfig,
) {
    private val connectionService = GoogleSheetsApiConnectionService(
        config.credentialsPath,
        listOf(SheetsScopes.SPREADSHEETS_READONLY)
    )

    private fun parseRatingTable(ratingTable: List<List<String>>, ranges: RatingRanges): List<RatingRow> =
        ratingTable.asSequence().map { row ->
            println(row)
            RatingRow(
                row.getOrNull(ranges.firstNameColumn) ?: return@map null,
                row.getOrNull(ranges.lastNameColumn) ?: return@map null,
                row.getOrNull(ranges.totalProblemsColumn)?.parseFloatOrNull() ?: return@map null,
                row.getOrNull(ranges.totalPercentageColumn)?.parseFloatOrNull() ?: return@map null,
                row.getOrNull(ranges.totalRatingColumn)?.toInt() ?: return@map null,
                row.getOrNull(ranges.totalFailuresColumn)?.toInt() ?: return@map null,
                row.getOrNull(ranges.grobsColumn)?.toInt() ?: return@map null,
                row.getOrNull(ranges.grobsRatingColumn)?.toInt() ?: return@map null,
                row.getOrNull(ranges.algebraProblemsColumn)?.parseFloatOrNull() ?: return@map null,
                row.getOrNull(ranges.algebraPercentageColumn)?.toInt() ?: return@map null,
                row.getOrNull(ranges.numberTheoryProblemsColumn)?.parseFloatOrNull() ?: return@map null,
                row.getOrNull(ranges.numberTheoryPercentageColumn)?.toInt() ?: return@map null,
                row.getOrNull(ranges.combinatoricsProblemsColumn)?.parseFloatOrNull() ?: return@map null,
                row.getOrNull(ranges.combinatoricsPercentageColumn)?.toInt() ?: return@map null,
                row.getOrNull(ranges.geometryProblemsColumn)?.parseFloatOrNull() ?: return@map null,
                row.getOrNull(ranges.geometryPercentageColumn)?.toInt() ?: return@map null,
            )
        }.takeWhile { it != null }.map { it!! }.toList()


    fun getCountrysideRating(): List<RatingRow> =
        parseRatingTable(
            GoogleSheetsGetRequest(connectionService, config.ratingSpreadsheetId)
                .addRange(config.countrysideRatingRanges.range)
                .execute()
                .first(),
            config.countrysideRatingRanges,
        )

    fun getUrbanRating(): List<RatingRow> =
        parseRatingTable(
            GoogleSheetsGetRequest(connectionService, config.ratingSpreadsheetId)
                .addRange(config.urbanRatingRanges.range)
                .execute()
                .first(),
            config.urbanRatingRanges,
        )
}

internal fun String.parseFloatOrNull(): Float? = this.replace(',', '.').toFloatOrNull()
