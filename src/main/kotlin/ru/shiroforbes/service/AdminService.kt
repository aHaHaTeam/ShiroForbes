package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.config
import ru.shiroforbes.database.AdminDAO
import ru.shiroforbes.database.Admins
import ru.shiroforbes.model.Admin

interface AdminService {
    suspend fun getAdminById(id: Int): Admin?

    suspend fun getAdminByLogin(login: String): Admin?
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

    private fun daoToAdmin(dao: AdminDAO): Admin = Admin(dao.id.value, dao.name, dao.login, dao.password)

    override suspend fun getAdminById(id: Int): Admin? {
        return transaction {
            val dao = AdminDAO.findById(id) ?: return@transaction null
            return@transaction daoToAdmin(dao)
        }
    }

    override suspend fun getAdminByLogin(login: String): Admin? {
        return transaction {
            return@transaction daoToAdmin(
                AdminDAO.find { Admins.login eq login }.limit(1).let {
                    if (it.toList().isNotEmpty()) {
                        return@let it.first()
                    } else {
                        return@transaction null
                    }
                },
            )
        }
    }
}
