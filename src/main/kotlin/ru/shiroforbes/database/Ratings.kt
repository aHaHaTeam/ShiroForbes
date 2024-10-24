package ru.shiroforbes.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
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
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<RatingDAO2>(RatingSeason2)

    var date by RatingSeason2.date
    var student by RatingSeason2.student
    var points by RatingSeason2.points
    var total by RatingSeason2.total
    val algebra by RatingSeason2.algebra
    val numbersTheory by RatingSeason2.numbersTheory
    val geometry by RatingSeason2.geometry
    val combinatorics by RatingSeason2.combinatorics

    var totalPercent by RatingSeason2.totalPercent
    var algebraPercent by RatingSeason2.algebraPercent
    var numbersTheoryPercent by RatingSeason2.numbersTheoryPercent
    var geometryPercent by RatingSeason2.geometryPercent
    var combinatoricsPercent by RatingSeason2.combinatoricsPercent

    var grobs by RatingSeason2.grobs
    val position by RatingSeason2.position
}
