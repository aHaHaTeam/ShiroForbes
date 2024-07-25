package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.database.*
import ru.shiroforbes.model.Student

// Service for accessing the student storage.
// It is very likely that this interface will be changed/expanded depending on future requirements.
interface StudentService {
    suspend fun getStudentById(id: Int): Student?

    suspend fun getStudentByLogin(login: String): Student?

    suspend fun getAllStudents(): List<Student> = throw NotImplementedError() // TODO

    suspend fun updateStudent(id: Int): Student = throw NotImplementedError() // TODO
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
        val ratings = dao.ratingHistory.toList()
        val wealths = dao.wealthHistory.toList()
        return Student(dao, ratings, wealths)
    }

    override suspend fun getStudentByLogin(login: String): Student? {
        return daoToStudent(StudentDAO.find { Students.login eq login }.limit(1).let {
            if (it.toList().isNotEmpty()) {
                return@let it.first()
            } else {
                return null
            }
        })
    }

    override suspend fun getAllStudents(): List<Student> {
        return StudentDAO.all().map { daoToStudent(it) }
    }
}
