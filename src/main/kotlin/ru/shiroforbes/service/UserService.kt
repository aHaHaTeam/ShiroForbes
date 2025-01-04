package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import ru.shiroforbes.config
import ru.shiroforbes.model.User

interface UserService {
    suspend fun getPasswordByLogin(login: String): String?

    suspend fun getUserByLogin(login: String): User?
}

object DbUserService : UserService {
    init {
        Database.connect(
            config.dbConfig.connectionUrl,
            config.dbConfig.driver,
            config.dbConfig.user,
            config.dbConfig.password,
        )
    }

    override suspend fun getPasswordByLogin(login: String): String? {
        val student = DbStudentService.getPasswordByLogin(login)
        if (student == null) {
            return DbAdminService.getPasswordByLogin(login)
        }
        return student
    }

    override suspend fun getUserByLogin(login: String): User? {
        val student = DbStudentService.getStudentStatByLogin(login)
        if (student == null) {
            return DbAdminService.getAdminByLogin(login)
        }
        return student
    }
}
