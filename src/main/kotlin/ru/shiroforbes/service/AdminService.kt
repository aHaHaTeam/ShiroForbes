package ru.shiroforbes.service

import ru.shiroforbes.model.AdminStat

interface AdminService {
    suspend fun getAdminByLogin(login: String): AdminStat?
    suspend fun getPasswordByLogin(login: String): String?
}
