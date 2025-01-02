@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.service

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.shiroforbes.config
import ru.shiroforbes.database.RatingDAO2
import ru.shiroforbes.database.RatingSeason2
import ru.shiroforbes.database.StudentDAO2
import ru.shiroforbes.database.StudentSeason2
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.modules.googlesheets.RatingRow

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
            student?.ratings =
                RatingDAO2
                    .find {
                        RatingSeason2.student eq student!!.id.value
                    }.toList()
                    .sortedByDescending { it.date }
            return@transaction student
        }
    }

    fun updateRatingSeason2(
        pos: Int,
        row: RatingRow,
    ) {
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

                it[totalPercent] = row.solvedPercentage.toInt() // TODO
                it[algebra] = row.algebraSolved
                it[numbersTheory] = row.numbersTheorySolved
                it[geometry] = row.geometrySolved
                it[combinatorics] = row.combinatoricsSolved

                it[grobs] = row.grobs
                it[position] = pos
                it[date] =
                    Clock.System
                        .now()
                        .toLocalDateTime(TimeZone.currentSystemDefault())
            }
        }
    }

    fun updateGroupSeason2(
        name: String,
        group: GroupType,
    ) {
        transaction {
            val student = getStudentByNameSeason2(name)
            if (student == null) {
                exposedLogger.debug("Updating rating")
                return@transaction
            }

            StudentSeason2.update({ StudentSeason2.id eq student.id.value }) {
                it[StudentSeason2.group] = group == GroupType.Urban
            }
        }
    }
}
