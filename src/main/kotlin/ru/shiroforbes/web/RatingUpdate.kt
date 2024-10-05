package ru.shiroforbes.web

import ru.shiroforbes.database.RatingDAO2
import ru.shiroforbes.database.StudentDAO2
import ru.shiroforbes.model.RatingDelta
import ru.shiroforbes.modules.googlesheets.RatingRow
import ru.shiroforbes.service.DbStudentService

fun computeRatingDeltas(newRatings: List<RatingRow>): List<RatingDelta> {
    val stringRatingsMap =
        newRatings.associateBy {
            it.lastName.trim() + " " + it.firstName.trim()
        }
    val current = mutableListOf<StudentDAO2>()
    stringRatingsMap.forEach {
        DbStudentService.getStudentByNameSeason2(it.key)?.let { it1 -> current.add(it1) }
    }
    return current
        .filter { stringRatingsMap.containsKey(it.name) }
        .sortedByDescending { it.getScore() }
        .mapIndexed { i, student ->
            println(student.name)
            RatingDelta(
                student.name,
                i + 1,
                -1,
                stringRatingsMap[student.name]!!.solvedProblems,
                stringRatingsMap[student.name]!!.solvedProblems - student.getTotal(),
                stringRatingsMap[student.name]!!.rating,
                stringRatingsMap[student.name]!!.rating - student.getScore(),
            )
        }.sortedByDescending { it.rating }
        .mapIndexed { i, student ->
            student.copy(newRank = i + 1)
        }
}

suspend fun updateRating(rating: List<RatingRow>) {
    rating.forEach {
        DbStudentService
            .updateRatingSeason2(
                RatingDAO2(it),
            )
    }
}
