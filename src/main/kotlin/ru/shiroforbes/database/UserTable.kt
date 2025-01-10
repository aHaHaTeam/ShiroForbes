package ru.shiroforbes.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import ru.shiroforbes.model.Rights

object UserTable : IntIdTable("user", "user_id") {
    val login: Column<String> = varchar("login", 255)
    val password = varchar("password", 255)
    val rights = enumerationByName("rights", 32, Rights::class)
}