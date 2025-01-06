package ru.shiroforbes.service

import ru.shiroforbes.model.Student
import ru.shiroforbes.model.StudentStat

interface StudentService {
    fun getStudentStatById(id: Int): StudentStat?
    fun getAllStudentsByName(): Map<String, StudentStat>
    fun getStudentStatByName(name: String): StudentStat?
    fun getStudentStatByLogin(login: String): StudentStat?
    fun getStudentByLogin(login: String): Student?
    fun getPasswordByLogin(login: String): String?
}