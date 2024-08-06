package ru.shiroforbes.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import ru.shiroforbes.model.GroupType

object Events : IntIdTable("events", "event_id") {
    var group: Column<GroupType> =
        customEnumeration(
            "group",
            "varchar(20)",
            { value ->
                GroupType.entries.find { it.text == value } ?: throw IllegalArgumentException("Unknown groupType value")
            },
            { it.text },
        )
    val name = varchar("name", 100)
    val timeAndPlace = varchar("timeAndPlace", 200)
    val description = varchar("description", 1000)
}

class EventDAO(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<EventDAO>(Events)

    var group by Events.group
    var name by Events.name
    var timeAndPlace by Events.timeAndPlace
    var description by Events.description
}
