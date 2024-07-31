package ru.shiroforbes.config

data class GoogleSheetsConfig(
    val ratingSpreadsheetId: String,
    val ratingRanges: List<String>,
)
