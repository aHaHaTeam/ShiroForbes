package ru.shiroforbes.service

import ru.shiroforbes.model.Group
import ru.shiroforbes.model.Student

interface StudentService {
    fun getNumberOfStudentsInGroup(group: Group): Int

    fun getAllStudentsByName(): Map<String, Student>

    fun getStudentStatByLogin(login: String): Student?

    fun getStudentByLogin(login: String): Student?
}
