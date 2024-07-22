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

class RatingDAO(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<RatingDAO>(Ratings)

    val date by Ratings.date
    val total by Ratings.total
    val algebra by Ratings.algebra
    val geometry by Ratings.geometry
    val combinatorics by Ratings.combinatorics
}
