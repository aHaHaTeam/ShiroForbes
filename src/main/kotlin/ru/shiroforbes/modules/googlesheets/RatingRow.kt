package ru.shiroforbes.modules.googlesheets

data class RatingRow(
    val firstName: String,
    val lastName: String,
    val solvedProblems: Int,
    val rating: Int,
    val algebraPercentage: Int,
    val combinatoricsPercentage: Int,
    val geometryPercentage: Int,
)
