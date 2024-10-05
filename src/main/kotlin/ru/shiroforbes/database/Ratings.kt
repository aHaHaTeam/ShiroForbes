package ru.shiroforbes.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object Ratings : IntIdTable("rating", "rating_id") {
    val date = date("date")
    val total = float("total")
    val algebra = float("algebra")
    val geometry = float("geometry")
    val combinatorics = float("combinatorics")
}

class RatingDAO(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<RatingDAO>(Ratings)

    val date by Ratings.date
    val total by Ratings.total
    val algebra by Ratings.algebra
    val geometry by Ratings.geometry
    val combinatorics by Ratings.combinatorics
}

object RatingSeason2 : IntIdTable("ratings_season2", "ratingId") {
    val date = date("date")
    val student = integer("studentId")
    val points = integer("points")
    val total = integer("total")
    val algebra = integer("algebra")
    val numbersTheory = integer("numbers_theory")
    val geometry = integer("geometry")
    val combinatorics = integer("combinatorics")

    val totalPercent = integer("total_percent")
    val algebraPercent = integer("algebra_percent")
    val numbersTheoryPercent = integer("numbers_theory_percent")
    val geometryPercent = integer("geometry_percent")
    val combinatoricsPercent = integer("combinatorics_percent")
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
}
