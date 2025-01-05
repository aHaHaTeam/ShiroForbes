package ru.shiroforbes.service

import ru.shiroforbes.database.StudentStat
import ru.shiroforbes.model.Student

interface StudentService {
    fun getStudentStatById(id: Int): StudentStat?
    fun getAllStudentsByName(): Map<String, StudentStat>
    fun getStudentStatByName(name: String): StudentStat?
    fun getStudentStatByLogin(login: String): StudentStat?
    suspend fun getStudentByLogin(login: String): Student
    fun getPasswordByLogin(login: String): String?
}