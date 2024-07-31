package ru.shiroforbes.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Events: IntIdTable("events", "event_id"){
    val name = varchar("name", 100)
    val timeAndPlace = varchar("timeAndPlace",200)
    val description = varchar("description", 1000)
}

class EventDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventDAO>(Events)

    var name by Events.name
    var timeAndPlace by Events.timeAndPlace
    var description by Events.description
}