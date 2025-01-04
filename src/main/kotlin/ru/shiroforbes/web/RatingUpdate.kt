package ru.shiroforbes.web

import org.jetbrains.exposed.sql.exposedLogger
import ru.shiroforbes.database.StudentStat
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.model.RatingDelta
import ru.shiroforbes.modules.googlesheets.RatingRow
import ru.shiroforbes.service.DbRatingService
import ru.shiroforbes.service.DbStudentService
import kotlin.math.round

fun computeRatingDeltas(newRatings: List<RatingRow>): List<RatingDelta> {
    val stringRatingsMap =
        newRatings.associateBy {
            it.lastName.trim() + " " + it.firstName.trim()
        }
    val current = mutableListOf<StudentStat>()
    val dbstudents = DbStudentService.getAllStudentsByName()
    stringRatingsMap.forEach {
        dbstudents[it.key].let { it1 ->
            if (it1 != null) current.add(it1) else exposedLogger.debug("${it.key} not found")
        }
    }
    return current
        .asSequence()
        .filter { stringRatingsMap.containsKey(it.name) }
        .sortedByDescending { it.score }
        .mapIndexed { i, student ->
            println(student.name)
            RatingDelta(
                student.name,
                student.login,
                i + 1,
                -1,
                stringRatingsMap[student.name]!!.solvedProblems,
                ((stringRatingsMap[student.name]!!.solvedProblems - student.total)).round(1),
                stringRatingsMap[student.name]!!.rating,
                stringRatingsMap[student.name]!!.rating - student.score,
            )
        }.sortedByDescending { it.rating }
        .mapIndexed { i, student ->
            student.copy(newRank = i + 1)
        }.toList()
}

suspend fun updateRating(rating: List<RatingRow>) {
    DbRatingService.updateRatingAll(rating)
}

fun updateGroup(
    rating: List<RatingRow>,
    group: GroupType,
) {
    DbRatingService.updateGroupAll(rating.map { it.name() }, group)
}

fun Float.round(decimals: Int): Float {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (round(this * multiplier) / multiplier).toFloat()
}
