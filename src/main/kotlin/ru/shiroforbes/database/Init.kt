package ru.shiroforbes.database

import com.google.api.services.sheets.v4.SheetsScopes
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
import ru.shiroforbes.modules.googlesheets.GoogleSheetsApiConnectionService
import ru.shiroforbes.modules.googlesheets.GoogleSheetsService
import ru.shiroforbes.service.DbStudentService
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

data class ConversionClassAdmin(
    val id: Int,
    val name: String = "",
    val login: String = "",
    val password: String = "",
    val group: GroupType,
)

internal fun kotlin.String.toFloatOrNull(): Float? =
    try {
        println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        this
            .filter { !it.isWhitespace() }
            .split(',')
            .joinToString(".")
            .toFloat()
    } catch (e: Exception) {
        null
    }

internal fun kotlin.String.toGroupTypeOrNull(): GroupType? = GroupType.entries.find { it.text == this }

internal fun kotlin.String.toBooleanOrNull(): Boolean? =
    when (this) {
        "1" -> true
        "0" -> false
        "TRUE" -> true
        "FALSE" -> false
        "true" -> true
        "false" -> false
        "True" -> true
        "False" -> false
        else -> null
    }

fun main() {
    Database.connect(
        config.dbConfig.connectionUrl,
        config.dbConfig.driver,
        config.dbConfig.user,
        config.dbConfig.password,
    )

    transaction {
        drop(
            // Students,
            // Ratings,
            Admins,
            StudentSeason2,
            RatingSeason2,
        )
        create(
            // Students,
            // Ratings,
            Admins,
            StudentSeason2,
            RatingSeason2,
        )
        fetchGoogleSheets<ConversionClassStudent>("ShV!A2:N70", ConversionClassStudent::class).forEach { student ->
            val id =
                StudentSeason2.insertAndGetId {
                    it[name] = student.name
                    it[login] = student.login
                    it[password] = student.password
                }

            RatingSeason2.insert {
                it[RatingSeason2.student] = id.value
                it[total] = 0F
                it[points] = 0F
                it[algebraPercent] = 0
                it[numbersTheoryPercent] = 0
                it[combinatoricsPercent] = 0
                it[geometryPercent] = 0

                it[totalPercent] = 0
                it[algebra] = 0
                it[numbersTheory] = 0
                it[geometry] = 0
                it[combinatorics] = 0
                it[date] =
                    Clock.System
                        .now()
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
            }
        }

        DbStudentService.getStudentByIdSeason2(1)
        fetchGoogleSheets<ConversionClassAdmin>("Admins!A2:E", ConversionClassAdmin::class).forEach { admin ->
            Admins.insert {
                it[name] = admin.name
                it[login] = admin.login
                it[password] = admin.password
            }
        }
    }
}

private fun <T : Any> fetchGoogleSheets(
    table: String,
    conversion: KClass<T>,
) = GoogleSheetsService(
    GoogleSheetsApiConnectionService(
        "/googlesheets/credentials.json",
        listOf(SheetsScopes.SPREADSHEETS_READONLY),
    ),
    "19fm18aFwdENQHXRu3ekG1GRJtiIe-k1-XCMgtMQXFSQ",
    conversion,
    listOf(
        table,
    ),
    Class.forName("ru.shiroforbes.database.InitKt"),
).getWhileNotEmpty()
