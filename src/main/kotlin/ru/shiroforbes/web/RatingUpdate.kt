package ru.shiroforbes.web

import kotlinx.datetime.toKotlinLocalDate
import ru.shiroforbes.model.Rating
import ru.shiroforbes.model.RatingDelta
import ru.shiroforbes.model.Student
import ru.shiroforbes.modules.googlesheets.RatingRow
import ru.shiroforbes.service.StudentService
import java.time.LocalDate

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
            println(student.name)
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

suspend fun updateRating(
    studentService: StudentService,
    rating: List<RatingRow>,
) {
    rating.forEach {
        studentService.addRating(
            Rating(
                -1,
                -1,
                LocalDate.now().toKotlinLocalDate(),
                it.solvedProblems,
                it.rating,
                0f,
                it.algebraPercentage.toFloat(),
                it.geometryPercentage.toFloat(),
                it.combinatoricsPercentage.toFloat(),
            ),
            it.lastName.trim() + " " + it.firstName.trim(),
        )
        studentService.updateRating(
            it.lastName.trim() + " " + it.firstName.trim(),
            it.rating,
            it.solvedProblems,
            it.algebraPercentage,
            it.geometryPercentage,
            it.combinatoricsPercentage,
        )
    }
}
