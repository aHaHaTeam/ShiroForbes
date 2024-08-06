package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.shiroforbes.config
import ru.shiroforbes.database.EventDAO
import ru.shiroforbes.database.Events
import ru.shiroforbes.model.Event

interface EventService {
    suspend fun getAllEvents(): List<Event>

    suspend fun getEvent(id: Int): Event?

    suspend fun addEvent(event: Event): Event

    suspend fun updateEvent(event: Event): Event
}

object DbEventService : EventService {
    init {
        Database.connect(
            config.dbConfig.connectionUrl,
            config.dbConfig.driver,
            config.dbConfig.user,
            config.dbConfig.password,
        )
    }

    private fun daoToEvent(dao: EventDAO): Event = Event(dao.id.value, dao.group, dao.name, dao.timeAndPlace, dao.description)

    override suspend fun getEvent(id: Int): Event? {
        return transaction {
            val dao = EventDAO.findById(id) ?: return@transaction null
            return@transaction daoToEvent(dao)
        }
    }

    override suspend fun getAllEvents(): List<Event> = transaction { EventDAO.all().map { daoToEvent(it) } }

    override suspend fun addEvent(event: Event): Event {
        return transaction {
            val id =
                Events.insertAndGetId {
                    it[name] = event.name
                    it[group] = event.group
                    it[timeAndPlace] = event.timeAndPlace
                    it[description] = event.description
                }
            return@transaction Event(
                id = id.value,
                group = event.group,
                name = event.name,
                timeAndPlace = event.timeAndPlace,
                description = event.description,
            )
        }
    }

    override suspend fun updateEvent(event: Event): Event {
        transaction {
            Events.update({ Events.id eq event.id }) {
                it[name] = event.name
                it[timeAndPlace] = event.timeAndPlace
                it[description] = event.description
            }
        }
        return event
    }
}
