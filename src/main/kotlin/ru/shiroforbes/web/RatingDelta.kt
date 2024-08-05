package ru.shiroforbes.web

import ru.shiroforbes.model.RatingDelta
import ru.shiroforbes.model.Student
import ru.shiroforbes.modules.googlesheets.RatingRow

fun computeRatingDeltas(
    current: List<Student>,
    newRatings: List<RatingRow>,
): List<RatingDelta> {
    val stringRatingsMap =
        newRatings.associateBy {
            it.lastName.trim() + " " + it.firstName.trim()
        }

    return current
        .sortedByDescending { it.rating }
        .mapIndexed { i, student ->
            RatingDelta(
                student.name,
                i + 1,
                i + 1,
                stringRatingsMap[student.name]!!.solvedProblems,
                stringRatingsMap[student.name]!!.solvedProblems - student.totalSolved,
                stringRatingsMap[student.name]!!.rating,
                stringRatingsMap[student.name]!!.rating - student.rating,
            )
        }.sortedByDescending { it.rating }
        .mapIndexed { i, student ->
            student.copy(newRank = i + 1)
        }
}
