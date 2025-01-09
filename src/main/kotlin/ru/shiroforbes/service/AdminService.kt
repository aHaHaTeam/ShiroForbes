package ru.shiroforbes.service

import ru.shiroforbes.model.Admin

interface AdminService {
    suspend fun getAdminByLogin(login: String): Admin?

    suspend fun getPasswordByLogin(login: String): String?
}
