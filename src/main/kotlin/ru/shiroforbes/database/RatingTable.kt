package ru.shiroforbes.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import ru.shiroforbes.model.Semester

object RatingTable : IntIdTable("ratings_season2", "ratingId") {
    val date = datetime("date")
    val semester = enumerationByName("semester", 32, Semester::class)
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
