package ru.shiroforbes.database

import org.jetbrains.exposed.sql.Table

object Students : Table("Students") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val login = varchar("login", 255)
    val password = varchar("password", 255)
    val rating = integer("rating")
    val wealth = integer("wealth")
    override val primaryKey = PrimaryKey(id)
}
