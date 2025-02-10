package ru.shiroforbes.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class Rating(
    var date: LocalDateTime,
    var student: Int,
    var points: Int,
    var total: Float,
    val algebra: Float,
    val numbersTheory: Float,
    val geometry: Float,
    val combinatorics: Float,
    var totalPercent: Int,
    var algebraPercent: Int,
    var numbersTheoryPercent: Int,
    var geometryPercent: Int,
    var combinatoricsPercent: Int,
    var grobs: Int,
    val position: Int,
)
