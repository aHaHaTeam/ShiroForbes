package ru.shiroforbes.modules.googlesheets

class GoogleSheetsGetRequest(
    private val connectionService: GoogleSheetsApiConnectionService,
    private val spreadsheetId: String,
) {
    private val tableRanges = mutableListOf<String>()

    fun addRange(range: String): GoogleSheetsGetRequest {
        tableRanges.add(range)
        return this
    }

    fun execute(): List<List<List<String>>> {
        val response =
            connectionService
                .service
                .spreadsheets()
                .values()
                .batchGet(spreadsheetId)
                .apply { ranges = tableRanges }
                .execute()
        return response.valueRanges.map { range ->
            range.getValues().map { row -> row.map { it.toString() } }
        }
    }
}