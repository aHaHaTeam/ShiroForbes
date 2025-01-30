package ru.shiroforbes.modules.googlesheets

import ru.shiroforbes.config.GoogleSheetsConfig

class RatingLoaderService(
    private val config: GoogleSheetsConfig,
) {
    private val connectionService = GoogleSheetsConnectionService(
        config.credentialsPath
    )

    private val parser = ReflectiveTableParser(RatingRow::class, listOf(CustomDecoder(), DefaultDecoder()))

    fun getCountrysideRating(): List<RatingRow> = getRating(config.countrysideRatingRanges)
    fun getCountrysideRatingSemester2(): List<RatingRow> = getRating(config.countrysideRatingRangesSemester2)

    fun getUrbanRating(): List<RatingRow> = getRating(config.urbanRatingRanges)
    fun getUrbanRatingSemester2(): List<RatingRow> = getRating(config.urbanRatingRangesSemester2)

    private fun getRating(ranges: List<String>): List<RatingRow> {
        val tables = ranges.fold(
            GoogleSheetsGetRequest(
                connectionService,
                config.ratingSpreadsheetId
            )
        ) { request, range -> request.addRange(range) }
            .execute()
        return parser.joinAndParse(ranges.map { tables[it]!! })
    }

}