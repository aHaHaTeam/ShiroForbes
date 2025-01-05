package ru.shiroforbes.service

import ru.shiroforbes.database.AdminStat
import ru.shiroforbes.model.Admin

interface AdminService {
    suspend fun getAdminById(id: Int): Admin?
    suspend fun getAdminStatById(id: Int): AdminStat?
    suspend fun getAdminByLogin(login: String): AdminStat?
    suspend fun getPasswordByLogin(login: String): String?
}
