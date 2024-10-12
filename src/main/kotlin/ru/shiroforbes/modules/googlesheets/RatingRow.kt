package ru.shiroforbes.modules.googlesheets

data class RatingRow(
    val lastName: String,
    val firstName: String,
    val solvedProblems: Float,
    val rating: Float,
    val algebraPercentage: Int,
    val numbersTheoryPercentage: Int,
    val combinatoricsPercentage: Int,
    val geometryPercentage: Int,
) {
    fun name(): String = "$lastName $firstName"
}
