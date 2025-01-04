package ru.shiroforbes.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import ru.shiroforbes.model.User

object Admins : IntIdTable("admin", "admin_id") {
    val name: Column<String> = varchar("name", 200)
    val login = varchar("login", 255)
    val password = varchar("password", 255)
}

class AdminStat(
    var name: String,
    login: String,
) : User(login, true)
