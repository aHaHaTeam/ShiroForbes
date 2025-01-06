package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.database.AdminTable
import ru.shiroforbes.model.AdminStat

class DbAdminService(
    private val database: Database,
) : AdminService {
    override suspend fun getAdminByLogin(login: String): AdminStat? =
        transaction(database) {
            AdminTable
                .select(AdminTable.name, AdminTable.login)
                .where(AdminTable.login.eq(login))
                .map { row -> AdminStat(row[AdminTable.name], row[AdminTable.login]) }
                .singleOrNull()
        }

    override suspend fun getPasswordByLogin(login: String): String? =
        transaction(database) {
            AdminTable
                .select(AdminTable.password)
                .where(AdminTable.login.eq(login))
                .map { row -> row[AdminTable.password] }
                .singleOrNull()
        }
}
