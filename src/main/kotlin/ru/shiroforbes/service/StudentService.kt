package ru.shiroforbes.service

import ru.shiroforbes.model.Student

interface StudentService {
    fun getStudentStatById(id: Int): Student?

    fun getAllStudentsByName(): Map<String, Student>

    fun getStudentStatByName(name: String): Student?

    fun getStudentStatByLogin(login: String): Student?

    fun getStudentByLogin(login: String): Student?

    fun getPasswordByLogin(login: String): String?
}
