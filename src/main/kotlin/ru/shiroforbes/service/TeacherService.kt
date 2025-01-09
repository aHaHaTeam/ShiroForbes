package ru.shiroforbes.service

import ru.shiroforbes.model.Teacher

interface TeacherService {
    suspend fun getTeacherByLogin(login: String): Teacher?

    suspend fun getPasswordByLogin(login: String): String?
}
