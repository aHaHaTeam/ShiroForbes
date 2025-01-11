package ru.shiroforbes.config

data class GoogleSheetsConfig(
    val credentialsPath: String,
    val initSpreadsheetId: String,
    val ratingSpreadsheetId: String,
    val countrysideRatingRanges: RatingRanges,
    val urbanRatingRanges: RatingRanges,
)

data class RatingRanges(
    val range: String,
    val firstNameColumn: Int,
    val lastNameColumn: Int,
    val totalProblemsColumn: Int,
    val totalPercentageColumn: Int,
    val totalRatingColumn: Int,
    val totalFailuresColumn: Int,
    val grobsColumn: Int,
    val grobsRatingColumn: Int,
    val algebraProblemsColumn: Int,
    val algebraPercentageColumn: Int,
    val numberTheoryProblemsColumn: Int,
    val numberTheoryPercentageColumn: Int,
    val combinatoricsProblemsColumn: Int,
    val combinatoricsPercentageColumn: Int,
    val geometryProblemsColumn: Int,
    val geometryPercentageColumn: Int,
)
