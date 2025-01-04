package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.config
import ru.shiroforbes.database.AdminStat
import ru.shiroforbes.database.Admins
import ru.shiroforbes.model.Admin

interface AdminService {
    suspend fun getAdminById(id: Int): Admin?

    suspend fun getAdminStatById(id: Int): AdminStat?

    suspend fun getAdminByLogin(login: String): AdminStat?

    suspend fun getPasswordByLogin(login: String): String?
}

object DbAdminService : AdminService {
    init {
        Database.connect(
            config.dbConfig.connectionUrl,
            config.dbConfig.driver,
            config.dbConfig.user,
            config.dbConfig.password,
        )
    }

    override suspend fun getAdminById(id: Int): Admin {
        val dao = getAdminById(id)
        return Admin(dao.name, dao.login)
    }

    override suspend fun getAdminStatById(id: Int): AdminStat? =
        transaction {
            Admins
                .select(Admins.name, Admins.login)
                .where(Admins.id.eq(id))
                .map { row -> AdminStat(row[Admins.name], row[Admins.login]) }
                .singleOrNull()
        }

    override suspend fun getAdminByLogin(login: String): AdminStat? =
        transaction {
            Admins
                .select(Admins.name, Admins.login)
                .where(Admins.login.eq(login))
                .map { row -> AdminStat(row[Admins.name], row[Admins.login]) }
                .singleOrNull()
        }

    override suspend fun getPasswordByLogin(login: String): String? =
        transaction {
            Admins
                .select(Admins.password)
                .where(Admins.login.eq(login))
                .map { row -> row[Admins.password] }
                .singleOrNull()
        }
}
