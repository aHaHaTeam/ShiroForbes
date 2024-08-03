package ru.shiroforbes.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import ru.shiroforbes.model.GroupType

object Admins : IntIdTable("admin", "admin_id") {
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
}

class AdminDAO(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<AdminDAO>(Admins)

    var name by Admins.name
    var login: String by Admins.login
    var password: String by Admins.password
    var group: GroupType by Admins.group
}
