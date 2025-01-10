package ru.shiroforbes.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object AdminTable : IntIdTable("admin", "admin_id") {
    val name: Column<String> = varchar("name", 200)
    val login = varchar("login", 255)
    val password = varchar("password", 255)
}
