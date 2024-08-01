package ru.shiroforbes.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import ru.shiroforbes.model.GroupType

object Students : IntIdTable("student", "student_id") {
    val name: Column<String> = varchar("name", 200)
    val login = varchar("login", 255)
    val password = varchar("password", 255)

    var group: Column<GroupType> =
        customEnumeration(
            "group",
            "varchar(20)",
            { value ->
                GroupType.entries.find { it.text == value } ?: throw IllegalArgumentException("Unknown groupType value")
            },
            { it.text },
        )

    val rating = integer("rating")
    val wealth = integer("wealth")

    val totalSolved = integer("totalSolved")
    val algebraSolved = integer("algebraSolved")
    val geometrySolved = integer("geometrySolved")
    val combinatoricsSolved = integer("combinatoricsSolved")

    val isExercised = bool("exercised").nullable()
    val isBeaten = bool("beaten").nullable()
    val isInvesting = bool("investing").nullable()
}

class StudentDAO(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<StudentDAO>(Students)

    var name by Students.name
    var login: String by Students.login
    var password: String by Students.password
    var group: GroupType by Students.group
    val rating: Int by Students.rating
    val wealth: Int by Students.wealth

    val totalSolved: Int by Students.totalSolved
    val algebraSolved: Int by Students.algebraSolved
    val geometrySolved: Int by Students.geometrySolved
    val combinatoricsSolved: Int by Students.combinatoricsSolved

    val isExercised: Boolean? by Students.isExercised
    val isBeaten: Boolean? by Students.isBeaten
    val isInvesting: Boolean? by Students.isInvesting

    val ratingHistory by RatingDAO via StudentRatings
    val wealthHistory by WealthDAO via StudentWealth

    val transactions by TransactionDAO via StudentTransaction
}
