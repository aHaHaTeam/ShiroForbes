package ru.shiroforbes.model

data class StudentDelta(
    val name: String,
    val oldRank: Int,
    val newRank: Int,
    val solved: Int,
    val solvedDelta: Int,
    val rating: Int,
    val ratingDelta: Int,
)
