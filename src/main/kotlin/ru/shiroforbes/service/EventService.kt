package ru.shiroforbes.service

import ru.shiroforbes.model.Event

interface EventService {
    fun getAllEvents(): List<Event>

    fun getEvent(id: Int): Event?

    fun addEvent(event: Event): Event

    fun updateEvent(event: Event): Event
}
