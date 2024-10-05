package ru.shiroforbes.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Admins : IntIdTable("admin", "admin_id") {
    val name: Column<String> = varchar("name", 200)
    val login = varchar("login", 255)
    val password = varchar("password", 255)
}

class AdminDAO(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<AdminDAO>(Admins)

    var name by Admins.name
    var login: String by Admins.login
    var password: String by Admins.password
}
