@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.service

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.config
import ru.shiroforbes.database.*
import ru.shiroforbes.model.Rating

/**
 * Service for accessing the student storage.
 * It is very likely that this interface will be changed/expanded depending on future requirements.
 */
interface StudentService {
    suspend fun updateRating(
        name: String,
        rating: Int,
        solved: Int,
        algebra: Int,
        combinatorics: Int,
        geometry: Int,
    ): Unit = throw NotImplementedError()

    suspend fun addRating(
        rating: Rating,
        name: String,
    ): Unit = throw NotImplementedError()
}

object DbStudentService {
    init {
        Database.connect(
            config.dbConfig.connectionUrl,
            config.dbConfig.driver,
            config.dbConfig.user,
            config.dbConfig.password,
        )
    }

    fun getStudentByIdSeason2(id: Int): StudentDAO2? {
        return transaction {
            val students = StudentDAO2.find { StudentSeason2.id eq id }
            val student = if (students.empty()) null else students.first()
            student?.ratings = RatingDAO2.find { RatingSeason2.student eq id }.toList()
            return@transaction student
        }
    }

    fun getStudentByNameSeason2(name: String): StudentDAO2? {
        return transaction {
            val students = StudentDAO2.find { StudentSeason2.name eq name }
            val student = if (students.empty()) null else students.first()
            student?.ratings = RatingDAO2.find { RatingSeason2.student eq student!!.id }.toList()
            return@transaction student
        }
    }

    fun getStudentByLoginSeason2(login: String): StudentDAO2? {
        return transaction {
            val students = StudentDAO2.find { StudentSeason2.login eq login }
            val student = if (students.empty()) null else students.first()
            student?.ratings = RatingDAO2.find { RatingSeason2.student eq student!!.id }.toList()
            return@transaction student
        }
    }

    suspend fun getAllStudentsSeason2(): List<StudentDAO2> {
        // this func is broken and does not work,
        // but it is only better! Because every call of it may be a design error
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

    suspend fun updateRatingSeason2(ratingDAO2: RatingDAO2) {
        transaction {
            RatingSeason2.insert {
                it[student] = ratingDAO2.student
                it[total] = ratingDAO2.total
                it[algebra] = ratingDAO2.algebra
                it[numbersTheory] = ratingDAO2.numbersTheory
                it[combinatorics] = ratingDAO2.combinatorics
                it[geometry] = ratingDAO2.geometry

                it[totalPercent] = ratingDAO2.totalPercent
                it[algebraPercent] = ratingDAO2.algebraPercent
                it[numbersTheoryPercent] = ratingDAO2.numbersTheoryPercent
                it[combinatoricsPercent] = ratingDAO2.numbersTheoryPercent
                it[geometryPercent] = ratingDAO2.geometryPercent
            }
        }
    }
}
