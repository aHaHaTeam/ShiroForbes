@file:Suppress("ktlint:standard:filename")

package ru.shiroforbes.web

import ru.shiroforbes.model.Group
import ru.shiroforbes.model.Student
import ru.shiroforbes.service.GroupService
import ru.shiroforbes.service.StudentService

val students =
    listOf(
        Student(0, "Name1", "Login1", "Password1", 121, 1231),
        Student(1, "Name2", "Login2", "Password2", 122, 2232),
        Student(2, "Name3", "Login3", "Password3", 123, 3233),
        Student(3, "Name4", "Login4", "Password4", 124, 4234),
        Student(4, "Name5", "Login5", "Password5", 125, 5235),
        Student(5, "Name11", "Login11", "Password11", 21, 131),
        Student(6, "Name12", "Login12", "Password12", 22, 232),
        Student(7, "Name13", "Login13", "Password13", 23, 333),
    )

object MockStudentService : StudentService {
    override suspend fun getStudent(id: Int): Student =
        if (id in 0..students.size) students[id] else throw RuntimeException("No student with id $id")

    override suspend fun getAllStudents(): List<Student> = students

    override suspend fun updateStudent(id: Int): Student = throw NotImplementedError()
}

val groups =
    mutableListOf(
        Group(
            0,
            "Regular camp",
            listOf(students[0], students[1], students[2], students[3], students[4]),
        ),
        Group(
            1,
            "City camp",
            listOf(students[5], students[6], students[7]),
        ),
    )

object MockGroupService : GroupService {
    override suspend fun getGroup(id: Int): Group =
        if (id in 0..<groups.size) groups[id] else throw RuntimeException("No group with id $id")

    override suspend fun getAllGroups(): List<Group> = groups
}
