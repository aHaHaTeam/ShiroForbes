package ru.shiroforbes.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object Wealths : IntIdTable("wealth", "wealth_id") {
    val date = date("date")
    val wealth = integer("wealth")
}

class WealthDAO(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<WealthDAO>(Wealths)

    val date by Wealths.date
    val wealth by Wealths.wealth
}
