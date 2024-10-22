@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.service

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.config
import ru.shiroforbes.database.*
import ru.shiroforbes.database.RatingSeason2.total
import ru.shiroforbes.model.Rating
import ru.shiroforbes.modules.googlesheets.RatingRow

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
            student?.ratings = RatingDAO2.find { RatingSeason2.student eq student!!.id.value }.toList()
            return@transaction student
        }
    }

    fun getStudentByLoginSeason2(login: String): StudentDAO2? {
        return transaction {
            val students = StudentDAO2.find { StudentSeason2.login eq login }
            val student = if (students.empty()) null else students.first()
            student?.ratings = RatingDAO2.find { RatingSeason2.student eq student!!.id.value }.toList().sortedByDescending { it.total }
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

    suspend fun updateRatingSeason2(row: RatingRow) {
        transaction {
            val student = getStudentByNameSeason2(row.name())
            if (student == null) {
                exposedLogger.debug("Updating rating")
                return@transaction
            }

            RatingSeason2.insert {
                it[RatingSeason2.student] = student.id.value
                it[total] = row.solvedProblems
                it[points] = row.rating
                it[algebraPercent] = row.algebraPercentage
                it[numbersTheoryPercent] = row.numbersTheoryPercentage
                it[combinatoricsPercent] = row.combinatoricsPercentage
                it[geometryPercent] = row.geometryPercentage

                it[totalPercent] = 0 // TODO
                it[algebra] = row.algebraSolved
                it[numbersTheory] = row.numbersTheorySolved
                it[geometry] = row.geometrySolved
                it[combinatorics] = row.combinatoricsSolved
                it[date] =
                    Clock.System
                        .now()
                        .toLocalDateTime(TimeZone.currentSystemDefault())
            }
        }
    }
}
