package ru.shiroforbes.model

data class RatingDelta(
    val name: String,
    val login: String,
    val oldRank: Int,
    val newRank: Int,
    val solved: Float,
    val solvedDelta: Float,
    val rating: Int,
    val ratingDelta: Int,
)
