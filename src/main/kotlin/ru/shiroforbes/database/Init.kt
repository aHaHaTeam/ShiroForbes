package ru.shiroforbes.database

import com.google.api.services.sheets.v4.SheetsScopes
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.config
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.modules.googlesheets.GoogleSheetsApiConnectionService
import ru.shiroforbes.modules.googlesheets.GoogleSheetsService
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
            Students,
            Ratings,
            Wealths,
            Transactions,
            StudentRatings,
            StudentWealth,
            StudentTransaction,
            Events,
            Admins,
        )
        create(
            Students,
            Ratings,
            Wealths,
            Transactions,
            StudentRatings,
            StudentWealth,
            StudentTransaction,
            Events,
            Admins,
        )
        fetchGoogleSheets<ConversionClassStudent>("ShV!A2:N70", ConversionClassStudent::class).forEach { student ->
            Students.insert {
                it[name] = student.name
                it[login] = student.login
                it[password] = student.password
                it[group] = student.group
                it[rating] = student.rating
                it[wealth] = student.wealth
                it[totalSolved] = student.totalSolved
                it[algebraSolved] = student.algebraSolved
                it[geometrySolved] = student.geometrySolved
                it[combinatoricsSolved] = student.combinatoricsSolved
                it[isExercised] = student.isExercised
                it[isBeaten] = student.isBeaten
                it[isInvesting] = student.isInvesting
            }
        }
        fetchGoogleSheets<ConversionClassAdmin>("Admins!A2:N70", ConversionClassAdmin::class).forEach { admin ->
            Admins.insert {
                it[name] = admin.name
                it[login] = admin.login
                it[password] = admin.password
                it[group] = admin.group
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
).getRating()
