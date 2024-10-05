package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import ru.shiroforbes.config
import ru.shiroforbes.model.User

interface UserService {
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

    override suspend fun getUserByLogin(login: String): User? {
        val student = DbStudentService.getStudentByLoginSeason2(login)
        var user: User? = if (student == null) null else User(student)
        if (user == null) {
            user = DbAdminService.getAdminByLogin(login)
        }
        return user
    }
}
