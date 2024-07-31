package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
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
            "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?prepareThreshold=0",
            driver = "org.postgresql.Driver",
            user = "postgres.lsbuufukrknwuixafuuu",
            password = "shiroforbes239",
        )
    }

    private fun daoToEvent(dao: EventDAO): Event {
        return Event(dao.id.value, dao.name, dao.timeAndPlace, dao.description)
    }

    override suspend fun getEvent(id: Int): Event? {
        return transaction {
            val dao = EventDAO.findById(id) ?: return@transaction null
            return@transaction daoToEvent(dao)
        }
    }

    override suspend fun getAllEvents(): List<Event> = EventDAO.all().map { daoToEvent(it) }

    override suspend fun addEvent(event: Event): Event {
        return transaction {
            val id = Events.insertAndGetId {
                it[name] = event.name
                it[timeAndPlace] = event.timeAndPlace
                it[description] = event.description
            }
            return@transaction Event(
                id = id.value,
                name = event.name,
                timeAndPlace = event.timeAndPlace,
                description = event.description
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