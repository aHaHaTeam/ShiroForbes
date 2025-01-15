package ru.shiroforbes.config

data class GoogleSheetsConfig(
    val credentialsPath: String,
    val initSpreadsheetId: String,
    val ratingSpreadsheetId: String,
    val countrysideRatingRanges: List<String>,
    val countrysideRatingRangesSemester2: List<String>,
    val urbanRatingRanges: List<String>,
    val urbanRatingRangesSemester2: List<String>,
)
