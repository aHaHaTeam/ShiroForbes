package ru.shiroforbes.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object StudentTable : IntIdTable("student_season2", "studentId") {
    val name: Column<String> = text("name")
    val group: Column<Boolean> = bool("group")
    val login: Column<String> = text("login")
    val password: Column<String> = text("password")
}
