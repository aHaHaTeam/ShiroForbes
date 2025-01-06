package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.database.TeacherTable
import ru.shiroforbes.model.TeacherStat

class DbTeacherService(
    private val database: Database,
) : TeacherService {
    override suspend fun getTeacherByLogin(login: String): TeacherStat? =
        transaction(database) {
            TeacherTable
                .select(TeacherTable.name, TeacherTable.login)
                .where(TeacherTable.login.eq(login))
                .map { row -> TeacherStat(row[TeacherTable.name], row[TeacherTable.login]) }
                .singleOrNull()
        }

    override suspend fun getPasswordByLogin(login: String): String? =
        transaction(database) {
            TeacherTable
                .select(TeacherTable.password)
                .where(TeacherTable.login.eq(login))
                .map { row -> row[TeacherTable.password] }
                .singleOrNull()
        }
}
