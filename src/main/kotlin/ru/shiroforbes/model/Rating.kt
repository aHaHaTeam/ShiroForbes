package ru.shiroforbes.model

import kotlinx.datetime.LocalDate

data class Rating(
    val id: Int,
    val studentId: Int,
    val date: LocalDate,
    val solved: Int,
    val rating: Int,
    val solvedPercentage: Float,
    val algebraPercentage: Float,
    val geometryPercentage: Float,
    val combinatoricsPercentage: Float,
)
