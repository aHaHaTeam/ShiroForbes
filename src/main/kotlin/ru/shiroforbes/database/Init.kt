package ru.shiroforbes.database

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.config
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.model.Rights
import ru.shiroforbes.modules.googlesheets.*
import kotlin.reflect.KClass

data class ConversionClassStudent(
    val id: Int,
    val name: String = "",
    val login: String = "",
    val password: String = "",
    val group: GroupType,
    val rating: Int = 0,
    val wealth: Int = 0,
    val totalSolved: Int = 0,
    val algebraSolved: Int = 0,
    val geometrySolved: Int = 0,
    val combinatoricsSolved: Int = 0,
    val isExercised: Boolean?,
    val isBeaten: Boolean?,
    val isInvesting: Boolean?,
)

data class ConversionClassTeacher(
    val id: Int,
    val name: String = "",
    val login: String = "",
    val password: String = "",
)

data class ConversionClassAdmin(
    val id: Int,
    val name: String = "",
    val login: String = "",
    val password: String = "",
)


fun main() {
    val database =
        Database.connect(
            config.dbConfig.connectionUrl,
            config.dbConfig.driver,
            config.dbConfig.user,
            config.dbConfig.password,
        )

    transaction(database) {
        exec("DROP TYPE IF EXISTS rights CASCADE;\n")
        exec("CREATE TYPE rights AS ENUM ('Admin', 'Teacher', 'Student');\n")

        drop(
            UserTable,
            AdminTable,
            TeacherTable,
            StudentTable,
            RatingTable,
            AchievementTable,
        )
        create(
            UserTable,
            AdminTable,
            TeacherTable,
            StudentTable,
            RatingTable,
            AchievementTable,
        )
        fetchGoogleSheets<ConversionClassStudent>("ShV!A2:N", ConversionClassStudent::class).forEach { student ->
            val id =
                StudentTable.insertAndGetId {
                    it[name] = student.name
                    it[login] = student.login
                    it[password] = student.password
                    it[group] = true
                }

            RatingTable.insert {
                it[RatingTable.student] = id.value
                it[total] = 0F
                it[points] = 0
                it[algebraPercent] = 0
                it[numbersTheoryPercent] = 0
                it[combinatoricsPercent] = 0
                it[geometryPercent] = 0
                it[grobs] = 0
                it[position] = 0

                it[totalPercent] = 0
                it[algebra] = 0f
                it[numbersTheory] = 0f
                it[geometry] = 0f
                it[combinatorics] = 0f
                it[date] =
                    Clock.System
                        .now()
                        .toLocalDateTime(TimeZone.currentSystemDefault())
            }

            UserTable.insert {
                it[login] = student.login
                it[password] = student.password
                it[rights] = Rights.Student
            }
        }

        fetchGoogleSheets("Teachers!A2:D", ConversionClassTeacher::class).forEach { teacher ->
            TeacherTable.insert {
                it[name] = teacher.name
                it[login] = teacher.login
                it[password] = teacher.password
            }

            UserTable.insert {
                it[login] = teacher.login
                it[password] = teacher.password
                it[rights] = Rights.Teacher
            }
        }

        fetchGoogleSheets("Admins!A2:D", ConversionClassAdmin::class).forEach { admin ->
            AdminTable.insert {
                it[name] = admin.name
                it[login] = admin.login
                it[password] = admin.password
            }

            UserTable.insert {
                it[login] = admin.login
                it[password] = admin.password
                it[rights] = Rights.Admin
            }
        }
    }
}

private fun <T : Any> fetchGoogleSheets(
    range: String,
    conversion: KClass<T>,
): List<T> {
    val table = GoogleSheetsGetRequest(
        GoogleSheetsConnectionService(
            config.googleSheetsConfig.credentialsPath,
        ),
        config.googleSheetsConfig.initSpreadsheetId,
    ).addRange(range).execute()
    return ReflectiveTableParser(conversion, listOf(CustomDecoder(), DefaultDecoder())).parse(table[range]!!)
}
