package ru.shiroforbes.service

import ru.shiroforbes.model.Group
import ru.shiroforbes.model.Rating
import ru.shiroforbes.model.Semester
import ru.shiroforbes.modules.googlesheets.RatingRow

interface RatingService {
    fun getRatings(login: String): Map<Semester, List<Rating>>
    suspend fun updateRating(list: List<RatingRow>, semester: Semester)
    fun updateGroup(
        names: List<String>,
        group: Group,
    )
}