package ru.shiroforbes.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import ru.shiroforbes.model.GroupType

object StudentTable : IntIdTable("student_season2", "studentId") {
    val name: Column<String> = text("name")
    val group = enumerationByName("group", 32, GroupType::class)
    val login: Column<String> = text("login")
    val password: Column<String> = text("password")
}
