package ru.shiroforbes.database

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.config
import ru.shiroforbes.model.Group
import ru.shiroforbes.model.Rights
import ru.shiroforbes.model.Semester
import ru.shiroforbes.modules.googlesheets.*
import kotlin.reflect.KClass

data class ConversionClassStudent(
    val id: Int,
    val name: String = "",
    val login: String = "",
    val password: String = "",
    val group: Group,
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
    val database = Database.connect(
        config.dbConfig.connectionUrl,
        config.dbConfig.driver,
        config.dbConfig.user,
        config.dbConfig.password,
    )

    transaction(database) {
        exec("DROP TYPE IF EXISTS \"rights\" CASCADE;\n")
        exec("CREATE TYPE \"rights\" AS ENUM ('Admin', 'Teacher', 'Student');\n")
        exec("DROP TYPE IF EXISTS \"semester\" CASCADE;\n")
        exec("CREATE TYPE \"semester\" AS ENUM ('Semester2', 'Semesters12');\n")
        exec("DROP TYPE IF EXISTS \"group\" CASCADE;\n")
        exec("CREATE TYPE \"group\" AS ENUM ('Countryside', 'Urban');\n")

        val allTables = arrayOf(
            UserTable,
            AdminTable,
            TeacherTable,
            StudentTable,
            RatingTable,
            AchievementTable,
        )

        drop(*allTables)
        create(*allTables)
    }

    runBlocking {
        launch {
            val students = fetchGoogleSheets("ShV!A2:N", ConversionClassStudent::class)
            addStudents(database, students)
        }
        launch {
            val teachers = fetchGoogleSheets("Teachers!A2:D", ConversionClassTeacher::class)
            addTeachers(database, teachers)
        }
        launch {
            val admins = fetchGoogleSheets("Admins!A2:D", ConversionClassAdmin::class)
            addAdmins(database, admins)
        }
    }
}

fun addStudents(database: Database, students: List<ConversionClassStudent>) =
    transaction(database) {
        students.forEach { student ->
            val id =
                StudentTable.insertAndGetId {
                    it[name] = student.name
                    it[login] = student.login
                    it[password] = student.password
                    it[group] = student.group
                }
            for (s in Semester.entries) RatingTable.insert {
                it[semester] = s
                it[date] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

                it[RatingTable.student] = id.value
                it[total] = 0f
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
            }

            UserTable.insert {
                it[login] = student.login
                it[password] = student.password
                it[rights] = Rights.Student
            }
        }
    }

fun addTeachers(database: Database, teachers: List<ConversionClassTeacher>) =
    transaction(database) {
        TeacherTable.batchInsert(teachers) { teacher ->
            this[TeacherTable.name] = teacher.name
            this[TeacherTable.login] = teacher.login
            this[TeacherTable.password] = teacher.password
        }
        UserTable.batchInsert(teachers) { teacher ->
            this[UserTable.login] = teacher.login
            this[UserTable.password] = teacher.password
            this[UserTable.rights] = Rights.Teacher
        }
    }

fun addAdmins(database: Database, admins: List<ConversionClassAdmin>) =
    transaction(database) {
        AdminTable.batchInsert(admins) { admin ->
            this[AdminTable.name] = admin.name
            this[AdminTable.login] = admin.login
            this[AdminTable.password] = admin.password
        }
        UserTable.batchInsert(admins) { admin ->
            this[UserTable.login] = admin.login
            this[UserTable.password] = admin.password
            this[UserTable.rights] = Rights.Admin
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
