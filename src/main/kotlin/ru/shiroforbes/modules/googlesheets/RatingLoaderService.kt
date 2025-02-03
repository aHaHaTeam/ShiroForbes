package ru.shiroforbes.modules.googlesheets

import ru.shiroforbes.config.GoogleSheetsConfig

class RatingLoaderService(
    private val config: GoogleSheetsConfig,
) {
    private val connectionService = GoogleSheetsConnectionService(
        config.credentialsPath
    )

    private val parser = ReflectiveTableParser(RatingRow::class, listOf(CustomDecoder(), DefaultDecoder()))

    fun getRating(ranges: List<String>): List<RatingRow> {
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