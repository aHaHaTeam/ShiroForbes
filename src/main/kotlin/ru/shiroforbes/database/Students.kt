package ru.shiroforbes.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import ru.shiroforbes.model.User

object StudentSeason2 : IntIdTable("student_season2", "studentId") {
    val name: Column<String> = text("name")
    val group: Column<Boolean> = bool("group")
    val login: Column<String> = text("login")
    val password: Column<String> = text("password")
}

class StudentStat(
    val name: String,
    val group: Boolean,
    login: String,
    val score: Int,
    val total: Float,
) : User(login, false)
