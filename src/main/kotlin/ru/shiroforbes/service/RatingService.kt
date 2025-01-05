package ru.shiroforbes.service

import ru.shiroforbes.database.RatingDAO2
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.modules.googlesheets.RatingRow

interface RatingService {
    suspend fun getRatings(login: String): List<RatingDAO2>
    suspend fun updateRatingAll(list: List<RatingRow>)
    fun updateGroupAll(
        names: List<String>,
        group: GroupType,
    )
}