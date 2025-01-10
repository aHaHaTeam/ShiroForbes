package ru.shiroforbes.service

import ru.shiroforbes.model.GroupType
import ru.shiroforbes.model.Rating
import ru.shiroforbes.modules.googlesheets.RatingRow

interface RatingService {
    fun getRatings(login: String): List<Rating>
    suspend fun updateRatingAll(list: List<RatingRow>)
    fun updateGroupAll(
        names: List<String>,
        group: GroupType,
    )
}