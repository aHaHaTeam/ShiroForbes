package ru.shiroforbes.database

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object RatingSeason2 : IntIdTable("ratings_season2", "ratingId") {
    val date = datetime("date")
    val student = integer("studentId")
    val points = integer("points")
    val total = float("total")
    val algebra = float("algebra")
    val numbersTheory = float("numbers_theory")
    val geometry = float("geometry")
    val combinatorics = float("combinatorics")

    val totalPercent = integer("total_percent")
    val algebraPercent = integer("algebra_percent")
    val numbersTheoryPercent = integer("numbers_theory_percent")
    val geometryPercent = integer("geometry_percent")
    val combinatoricsPercent = integer("combinatorics_percent")

    val grobs = integer("grobs")
    val position = integer("position")
}

class RatingDAO2(
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
