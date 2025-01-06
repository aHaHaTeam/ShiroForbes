package ru.shiroforbes.service

import ru.shiroforbes.model.TeacherStat

interface TeacherService {
    suspend fun getTeacherByLogin(login: String): TeacherStat?
    suspend fun getPasswordByLogin(login: String): String?
}