package ru.shiroforbes.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object AchievementTable : IntIdTable("achievements", "achievement_id") {
    val studentId = integer("student_id")
    val title: Column<String> = varchar("title", 200)
    val description = text("description")
    val icon = varchar("icon", 255)
    val classNumber = integer("class_number")
    var record = integer("record")
}
