package ru.shiroforbes.modules.googlesheets

data class RatingRow(
    val firstName: String,
    val lastName: String,
    val solvedProblems: String,
    val rating: String,
    val algebraPercentage: String,
    val combinatoricsPercentage: String,
    val geometryPercentage: Int,
)
