package ru.shiroforbes.modules.googlesheets

import ru.shiroforbes.config.GoogleSheetsConfig

class RatingLoaderService(
    private val config: GoogleSheetsConfig,
) {
    private val connectionService = GoogleSheetsConnectionService(
        config.credentialsPath
    )

    private val parser = ReflectiveTableParser(RatingRow::class, listOf(CustomDecoder(), DefaultDecoder()))

    fun getCountrysideRating(): List<RatingRow> {
        val tables = config.countrysideRatingRanges
            .fold(
                GoogleSheetsGetRequest(
                    connectionService,
                    config.ratingSpreadsheetId
                )
            ) { request, range -> request.addRange(range) }
            .execute()
        return parser.joinAndParse(config.countrysideRatingRanges.map { tables[it]!! })
    }


    fun getUrbanRating(): List<RatingRow> {
        val tables = config.urbanRatingRanges
            .fold(
                GoogleSheetsGetRequest(
                    connectionService,
                    config.ratingSpreadsheetId
                )
            ) { request, range -> request.addRange(range) }
            .execute()
        return parser.joinAndParse(config.urbanRatingRanges.map { tables[it]!! })
    }
}