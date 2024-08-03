@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.service

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.shiroforbes.database.StudentDAO
import ru.shiroforbes.database.Students
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.model.Student

/**
 * Service for accessing the student storage.
 * It is very likely that this interface will be changed/expanded depending on future requirements.
 */
interface StudentService {
    suspend fun getStudentById(id: Int): Student?

    suspend fun getStudentByLogin(login: String): Student?

    suspend fun getAllStudents(): List<Student> = throw NotImplementedError() // TODO

    suspend fun updateStudentInvesting(
        id: Int,
        investing: Boolean,
    ): Unit = throw NotImplementedError()

    suspend fun updateStudentExercised(
        id: Int,
        exercised: Boolean,
    ): Unit = throw NotImplementedError()

    suspend fun updateAllStudentsExercised(exercised: Boolean): Unit = throw NotImplementedError()

    suspend fun updateStudentBeaten(
        id: Int,
        beaten: Boolean,
    ): Unit = throw NotImplementedError()

    suspend fun updateAllStudentsBeaten(Beaten: Boolean): Unit = throw NotImplementedError()

    suspend fun getGroup(group: GroupType): List<Student> = throw NotImplementedError()
}

object DbStudentService : StudentService {
    init {
        Database.connect(
            "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?prepareThreshold=0",
            driver = "org.postgresql.Driver",
            user = "postgres.lsbuufukrknwuixafuuu",
            password = "shiroforbes239",
        )
    }

    override suspend fun getStudentById(id: Int): Student? {
        return transaction {
            val dao = StudentDAO.findById(id) ?: return@transaction null
            return@transaction daoToStudent(dao)
        }
    }

    private fun daoToStudent(dao: StudentDAO): Student {
        println("daoToStudent")
        val ratings = dao.ratingHistory.toList()
        val wealths = dao.wealthHistory.toList()
        return Student(dao, ratings, wealths)
    }

    override suspend fun getStudentByLogin(login: String): Student? {
        return transaction {
            return@transaction daoToStudent(
                StudentDAO.find { Students.login eq login }.limit(1).let {
                    if (it.toList().isNotEmpty()) {
                        return@let it.first()
                    } else {
                        return@transaction null
                    }
                },
            )
        }
    }

    override suspend fun getAllStudents(): List<Student> {
        println("getAllStudents")
        return StudentDAO.all().map { Student(it, listOf(), listOf()) } // we don't need information about rating and wealth history there
    }

    override suspend fun updateStudentInvesting(
        id: Int,
        investing: Boolean,
    ) {
        transaction {
            Students.update({ Students.id eq id }) {
                it[isInvesting] = investing
            }
        }
    }

    override suspend fun updateStudentExercised(
        id: Int,
        exercised: Boolean,
    ) {
        transaction {
            Students.update({ Students.id eq id }) {
                it[isExercised] = exercised
            }
        }
    }

    override suspend fun updateAllStudentsExercised(exercised: Boolean) {
        transaction {
            Students.update({ Students.id greater 0 }) {
                it[isExercised] = exercised
            }
        }
    }

    override suspend fun updateStudentBeaten(
        id: Int,
        beaten: Boolean,
    ) {
        transaction {
            Students.update({ Students.id eq id }) {
                it[isBeaten] = beaten
            }
        }
    }

    override suspend fun updateAllStudentsBeaten(Beaten: Boolean) {
        transaction {
            Students.update({ Students.id greater 0 }) {
                it[isBeaten] = Beaten
            }
        }
    }

    override suspend fun getGroup(group: GroupType): List<Student> =
        transaction {
            return@transaction runBlocking { getAllStudents().filter { it.group == group } }
        }
}
