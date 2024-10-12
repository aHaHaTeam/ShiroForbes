package ru.shiroforbes.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object StudentSeason2 : IntIdTable("student_season2", "studentId") {
    val name: Column<String> = text("name")
    val login: Column<String> = text("login")
    val password: Column<String> = text("password")
}

class StudentDAO2(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<StudentDAO2>(StudentSeason2)

    val name by StudentSeason2.name
    val login by StudentSeason2.login
    val password by StudentSeason2.password
    var ratings: List<RatingDAO2> = listOf()

    fun getScore(): Float =
        ratings.sortedByDescending { it.date }.let {
            if (it.isEmpty()) {
                return@let 0F
            }
            return@let it.first().points
        }

    fun getTotal(): Float =
        ratings.sortedByDescending { it.date }.let {
            if (it.isEmpty()) {
                return@let 0F
            }
            return@let it.first().total
        }
}
