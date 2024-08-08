package ru.shiroforbes.database

import com.google.api.services.sheets.v4.SheetsScopes
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.config
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.modules.googlesheets.GoogleSheetsApiConnectionService
import ru.shiroforbes.modules.googlesheets.GoogleSheetsService

data class ConversionClass(
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
            Offers,
        )
        Admins.deleteAll()
        StudentWealth.deleteAll()
        StudentTransaction.deleteAll()
        StudentRatings.deleteAll()
        Students.deleteAll()
        val students =
            GoogleSheetsService(
                GoogleSheetsApiConnectionService(
                    "/googlesheets/credentials.json",
                    listOf(SheetsScopes.SPREADSHEETS_READONLY),
                ),
                "19fm18aFwdENQHXRu3ekG1GRJtiIe-k1-XCMgtMQXFSQ",
                ConversionClass::class,
                listOf(
                    "ShV!A2:N61",
                ),
                Class.forName("ru.shiroforbes.database.InitKt"),
            ).getRating()
        for (student in students) {
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
        Admins.insert {
            it[name] = "vasya"
            it[login] = "vasya566"
            it[password] = "pass123"
            it[group] = GroupType.Urban
        }
    }
}
