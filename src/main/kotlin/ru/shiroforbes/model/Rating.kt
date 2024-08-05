package ru.shiroforbes.model

import kotlinx.datetime.LocalDate

data class Rating(
    val id: Int,
    val studentId: Int,
    val date: LocalDate,
    val total: Float,
    val algebra: Float,
    val geometry: Float,
    val combinatorics: Float,
)
