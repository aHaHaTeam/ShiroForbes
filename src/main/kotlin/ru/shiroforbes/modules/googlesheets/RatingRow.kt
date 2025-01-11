package ru.shiroforbes.modules.googlesheets

data class RatingRow(
    val firstName: String,
    val lastName: String,
    val solvedProblems: Float,
    val solvedPercentage: Float,
    val rating: Int,
    val failures: Int,
    val grobs: Int,
    val grobsRating: Int,
    val algebraSolved: Float,
    val algebraPercentage: Int,
    val numbersTheorySolved: Float,
    val numbersTheoryPercentage: Int,
    val combinatoricsSolved: Float,
    val combinatoricsPercentage: Int,
    val geometrySolved: Float,
    val geometryPercentage: Int,
) {
    fun name(): String = "$lastName $firstName"
}
