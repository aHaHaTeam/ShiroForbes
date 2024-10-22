package ru.shiroforbes.modules.googlesheets

data class RatingRow(
    val firstName: String,
    val lastName: String,
    val solvedProblems: Float,
    val rating: Float,
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
