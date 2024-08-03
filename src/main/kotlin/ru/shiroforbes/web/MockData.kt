@file:Suppress("ktlint:standard:filename")

package ru.shiroforbes.web

import ru.shiroforbes.model.Event
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.model.Student
import ru.shiroforbes.service.EventService
import ru.shiroforbes.service.StudentService
import java.io.File

val students =
    listOf(
        Student(1, "Name1", "Login1", "Password1", GroupType.Countryside, 121, 1231, 0, 0, 0, 0, null, false, false),
        Student(2, "Silly Cat Bleh", "Login2", "Password2", GroupType.Countryside, 239, 239239, 3, 1, 0, 2, false, true, true),
        Student(3, "Name3", "Login3", "Password3", GroupType.Countryside, 123, 3233, 0, 0, 0, 0, null, null, null),
        Student(4, "Name4", "Login4", "Password4", GroupType.Countryside, 124, 4234, 0, 0, 0, 0, true, false, null),
        Student(5, "Name5", "Login5", "Password5", GroupType.Countryside, 125, 5235, 0, 0, 0, 0, true, false, null),
        Student(6, "Name11", "Login11", "Password11", GroupType.Countryside, 21, 131, 0, 0, 0, 0, true, false, null),
        Student(7, "Name12", "Login12", "Password12", GroupType.Countryside, 22, 232, 0, 0, 0, 0, true, false, null),
        Student(8, "Name13", "Login13", "Password13", GroupType.Countryside, 23, 333, 0, 0, 0, 0, true, false, null),
    )

object MockStudentService : StudentService {
    override suspend fun getStudentById(id: Int): Student =
        if (id in 0..students.size) students[id] else throw RuntimeException("No student with id $id")

    override suspend fun getStudentByLogin(login: String): Student {
        students.find { it.login == login }?.let { return it } ?: throw RuntimeException("No student with login $login")
    }

    override suspend fun getAllStudents(): List<Student> = students

    // override suspend fun updateStudent(student: Student) = throw NotImplementedError()
}

val events =
    mutableListOf<Event>(
        Event(
            0,
            "Волейбол",
            "волейбольное поле, во время разбора",
            File("src/main/kotlin/ru/shiroforbes/web/MockEvent0Description.html").readText(),
        ),
        Event(
            1,
            "Волейбол",
            "волейбольное поле, во время разбора",
            File("src/main/kotlin/ru/shiroforbes/web/MockEvent0Description.html").readText(),
        ),
    )

object MockEventService : EventService {
    override suspend fun getAllEvents(): List<Event> = events

    override suspend fun getEvent(id: Int): Event? = events.find { it.id == id }

    override suspend fun addEvent(event: Event): Event {
        events.add(Event(events.size, event.name, event.timeAndPlace, event.description))
        return events.last()
    }

    override suspend fun updateEvent(event: Event): Event {
        events.removeIf { it.id == event.id }
        events.add(event)
        return event
    }
}
