package ru.shiroforbes.modules.googlesheets

data class RatingRow(
    val firstName: String,
    val lastName: String,
    val solvedProblems: Int,
    val rating: Int,
    val solvedPercentage: Float,
    val algebraPercentage: Float,
    val combinatoricsPercentage: Float,
    val geometryPercentage: Float,
)
