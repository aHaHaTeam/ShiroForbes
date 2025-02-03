package ru.shiroforbes.modules.googlesheets

data class RatingRow(
    val lastName: String,
    val firstName: String,
    val solvedProblems: Float,
    val solvedPercentage: Float,
    val rating: Int,
    val minuses: Int,
    val grobs: Int,
    val grobRating: Int,
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
