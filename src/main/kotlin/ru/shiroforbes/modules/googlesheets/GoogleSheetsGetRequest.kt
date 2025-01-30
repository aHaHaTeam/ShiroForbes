package ru.shiroforbes.modules.googlesheets

class GoogleSheetsGetRequest(
    private val connectionService: GoogleSheetsConnectionService,
    private val spreadsheetId: String,
) {
    private val tableRanges = mutableListOf<String>()

    fun addRange(range: String): GoogleSheetsGetRequest {
        tableRanges.add(range)
        return this
    }

    fun execute(): Map<String, List<List<String>>> {
        val response =
            connectionService
                .service
                .spreadsheets()
                .values()
                .batchGet(spreadsheetId)
                .apply {
                    ranges = tableRanges
                    valueRenderOption = "FORMATTED_VALUE"
                }
                .execute()
        return response.valueRanges.zip(tableRanges).associate { (table, range) ->
            range to table.getValues().map { row -> row.map { it.toString() } }
        }
    }
}