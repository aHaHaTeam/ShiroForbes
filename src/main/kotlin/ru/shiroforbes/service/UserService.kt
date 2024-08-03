package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import ru.shiroforbes.model.User

interface UserService {
    suspend fun getUserByLogin(login: String): User?
}

object DbUserService : UserService {
    init {
        Database.connect(
            "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?prepareThreshold=0",
            driver = "org.postgresql.Driver",
            user = "postgres.lsbuufukrknwuixafuuu",
            password = "shiroforbes239",
        )
    }

    override suspend fun getUserByLogin(login: String): User? {
        var user: User? = DbStudentService.getStudentByLogin(login)
        if (user == null) {
            user = DbAdminService.getAdminByLogin(login)
        }
        return user
    }
}
