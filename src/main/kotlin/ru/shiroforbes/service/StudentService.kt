@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.service

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.config
import ru.shiroforbes.database.*
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.model.Rating
import ru.shiroforbes.model.Student
import ru.shiroforbes.model.Wealth

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

    suspend fun updateRating(
        name: String,
        rating: Int,
        solved: Int,
        algebra: Int,
        combinatorics: Int,
        geometry: Int,
    ): Unit = throw NotImplementedError()

    suspend fun updateWealth(
        name: String,
        wealth: Int,
    ): Unit = throw NotImplementedError()

    suspend fun updateWealth(
        id: Int,
        wealth: Int,
    ): Unit = throw NotImplementedError()

    suspend fun getGroup(group: GroupType): List<Student> = throw NotImplementedError()

    suspend fun addRating(
        rating: Rating,
        name: String,
    ): Unit = throw NotImplementedError()

    suspend fun addWealth(
        wealth: Wealth,
        name: String,
    ): Unit = throw NotImplementedError()
}

object DbStudentService : StudentService {
    init {
        Database.connect(
            config.dbConfig.connectionUrl,
            config.dbConfig.driver,
            config.dbConfig.user,
            config.dbConfig.password,
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

    fun getStudentByIdSeason2(id: Int): StudentDAO2 {
        return transaction {
            val student = StudentDAO2.find { StudentSeason2.id eq id }.first()
            student.ratings = RatingDAO2.find { RatingSeason2.student eq id }.toList()
            return@transaction student
        }
    }

    suspend fun getAllStudentsSeason2(): List<StudentDAO2> {
        return transaction {
            return@transaction StudentSeason2
                .join(RatingSeason2, JoinType.LEFT, StudentSeason2.id, RatingSeason2.student)
                .selectAll()
                .groupBy({ StudentDAO2.wrapRow(it) }, { it })
                .map { (student, rows) ->
                    val ratings =
                        rows.map { row ->
                            RatingDAO2.wrapRow(row)
                        }
                    student.ratings = ratings
                    student
                }
        }
    }

    override suspend fun getAllStudents(): List<Student> =
        transaction {
            StudentDAO
                .all()
                .map {
                    Student(
                        it,
                        listOf(),
                        listOf(),
                    )
                } // we don't need information about rating and wealth history there
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

    override suspend fun updateRating(
        name: String,
        rating: Int,
        solved: Int,
        algebra: Int,
        combinatorics: Int,
        geometry: Int,
    ) {
        transaction {
            val studentId =
                StudentDAO
                    .find { Students.name eq name }
                    .limit(1)
                    .first()
                    .id
            Students.update({ Students.id eq studentId }) {
                it[this.rating] = rating
                it[totalSolved] = solved
                it[algebraSolved] = algebra
                it[combinatoricsSolved] = combinatorics
                it[geometrySolved] = geometry
            }
        }
    }

    override suspend fun updateWealth(
        name: String,
        wealth: Int,
    ) {
        transaction {
            val studentId =
                StudentDAO
                    .find { Students.name eq name }
                    .limit(1)
                    .first()
                    .id
            Students.update({ Students.id eq studentId }) {
                it[this.wealth] = wealth
            }
        }
    }

    override suspend fun updateWealth(
        id: Int,
        wealth: Int,
    ) {
        transaction {
            Students.update({ Students.id eq id }) {
                it[this.wealth] = wealth
            }
        }
    }

    override suspend fun getGroup(group: GroupType): List<Student> =
        transaction {
            return@transaction runBlocking { getAllStudents().filter { it.group == group } }
        }

    override suspend fun addRating(
        rating: Rating,
        name: String,
    ) {
        transaction {
            val studentId =
                StudentDAO
                    .find { Students.name eq name }
                    .limit(1)
                    .first()
                    .id
            val ratingId =
                Ratings.insertAndGetId {
                    it[date] = rating.date
                    it[total] = rating.solvedPercentage
                    it[algebra] = rating.algebraPercentage
                    it[geometry] = rating.geometryPercentage
                    it[combinatorics] = rating.combinatoricsPercentage
                }

            StudentRatings.insert {
                it[StudentRatings.rating] = ratingId
                it[student] = studentId
            }
        }
    }

    override suspend fun addWealth(
        wealth: Wealth,
        name: String,
    ) {
        transaction {
            val studentId =
                StudentDAO
                    .find { Students.name eq name }
                    .limit(1)
                    .first()
                    .id
            val wealthId =
                Wealths.insertAndGetId {
                    it[date] = wealth.date
                    it[Wealths.wealth] = wealth.wealth
                }

            StudentRatings.insert {
                it[rating] = wealthId
                it[student] = studentId
            }
        }
    }
}
