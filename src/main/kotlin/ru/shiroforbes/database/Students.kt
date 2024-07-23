package ru.shiroforbes.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Students : IntIdTable("student", "student_id") {
    val name: Column<String> = varchar("name", 200)
    val login = varchar("login", 255)
    val password = varchar("password", 255)
    val rating = integer("rating")
    val wealth = integer("wealth")
    val isExercised = bool("exercided")
    val isBeaten = bool("beaten")
    // TODO: add field isInvesting: Boolean
}

class StudentDAO(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<StudentDAO>(Students)

    var name by Students.name
    var login: String by Students.login
    val password: String by Students.password
    val rating: Int by Students.rating
    val wealth: Int by Students.wealth
    val isExercised: Boolean by Students.isExercised
    val isBeaten: Boolean by Students.isBeaten

    val ratingHistory by RatingDAO via StudentRatings
    val wealthHistory by WealthDAO via StudentWealth

    val transactions by TransactionDAO via StudentTransaction
}
