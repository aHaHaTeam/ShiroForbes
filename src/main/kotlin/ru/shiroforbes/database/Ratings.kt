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

object RatingSeason2 : IntIdTable("ratings_season2", "rating_id") {
    val date = date("date")
    val student = reference("id", StudentSeason2)
    val points = integer("points")
    val total = integer("total")
    val algebra = integer("algebra")
    val geometry = float("geometry")
    val combinatorics = float("combinatorics")

    val totalPercent = float("total_percent")
    val algebraPercent = float("algebra_percent")
    val geometryPercent = float("geometry_percent")
    val combinatoricsPercent = float("combinatorics_percent")
}

class RatingDAO2(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<RatingDAO2>(RatingSeason2)

    val date by RatingSeason2.date
    val student by RatingSeason2.student
    val points by RatingSeason2.points
    val total by RatingSeason2.total
    val algebra by RatingSeason2.algebra
    val geometry by RatingSeason2.geometry
    val combinatorics by RatingSeason2.combinatorics

    val totalPercent by RatingSeason2.totalPercent
    val algebraPercent by RatingSeason2.algebraPercent
    val geometryPercent by RatingSeason2.geometryPercent
    val combinatoricsPercent by RatingSeason2.combinatoricsPercent
}
