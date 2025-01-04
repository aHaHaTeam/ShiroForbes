package ru.shiroforbes.service

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.config
import ru.shiroforbes.database.RatingDAO2
import ru.shiroforbes.database.RatingSeason2
import ru.shiroforbes.database.RatingSeason2.algebra
import ru.shiroforbes.database.RatingSeason2.algebraPercent
import ru.shiroforbes.database.RatingSeason2.combinatorics
import ru.shiroforbes.database.RatingSeason2.combinatoricsPercent
import ru.shiroforbes.database.RatingSeason2.date
import ru.shiroforbes.database.RatingSeason2.geometry
import ru.shiroforbes.database.RatingSeason2.geometryPercent
import ru.shiroforbes.database.RatingSeason2.grobs
import ru.shiroforbes.database.RatingSeason2.numbersTheory
import ru.shiroforbes.database.RatingSeason2.numbersTheoryPercent
import ru.shiroforbes.database.RatingSeason2.points
import ru.shiroforbes.database.RatingSeason2.position
import ru.shiroforbes.database.RatingSeason2.student
import ru.shiroforbes.database.RatingSeason2.total
import ru.shiroforbes.database.RatingSeason2.totalPercent
import ru.shiroforbes.database.StudentSeason2
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.modules.googlesheets.RatingRow

object DbRatingService {
    init {
        Database.connect(
            config.dbConfig.connectionUrl,
            config.dbConfig.driver,
            config.dbConfig.user,
            config.dbConfig.password,
        )
    }

    suspend fun getRatings(login: String): List<RatingDAO2> =
        transaction {
            RatingSeason2
                .join(StudentSeason2, JoinType.INNER, RatingSeason2.student, StudentSeason2.id)
                .select(
                    date,
                    RatingSeason2.student,
                    points,
                    total,
                    algebra,
                    numbersTheory,
                    geometry,
                    combinatorics,
                    totalPercent,
                    algebraPercent,
                    numbersTheoryPercent,
                    geometryPercent,
                    combinatoricsPercent,
                    grobs,
                    position,
                ).where(StudentSeason2.login eq login)
                .map { resultRow ->
                    RatingDAO2(
                        resultRow.get(date),
                        resultRow.get(student),
                        resultRow.get(points),
                        resultRow.get(total),
                        resultRow.get(algebra),
                        resultRow.get(
                            numbersTheory,
                        ),
                        resultRow.get(geometry),
                        resultRow.get(combinatorics),
                        resultRow.get(totalPercent),
                        resultRow.get(
                            algebraPercent,
                        ),
                        resultRow.get(numbersTheoryPercent),
                        resultRow.get(
                            geometryPercent,
                        ),
                        resultRow.get(
                            combinatoricsPercent,
                        ),
                        resultRow.get(grobs),
                        resultRow.get(position),
                    )
                }
        }.sortedByDescending { it.date }

    suspend fun updateRatingAll(list: List<RatingRow>) {
        transaction {
            val ids =
                StudentSeason2
                    .select(StudentSeason2.id, StudentSeason2.name)
                    .associateBy({ it[StudentSeason2.name] }, { it[StudentSeason2.id] })
            val ranks =
                list
                    .sortedByDescending { it.rating }
                    .mapIndexed { i, row -> row.name() to i + 1 }
                    .associateBy({ it.first }, { it.second })
            RatingSeason2
                .batchInsert(list) { row ->
                    this[RatingSeason2.student] = ids[row.name()]!!.value
                    this[total] = row.solvedProblems
                    this[points] = row.rating
                    this[algebraPercent] = row.algebraPercentage
                    this[numbersTheoryPercent] = row.numbersTheoryPercentage
                    this[combinatoricsPercent] = row.combinatoricsPercentage
                    this[geometryPercent] = row.geometryPercentage

                    this[totalPercent] = row.solvedPercentage.toInt() // TODO
                    this[algebra] = row.algebraSolved
                    this[numbersTheory] = row.numbersTheorySolved
                    this[geometry] = row.geometrySolved
                    this[combinatorics] = row.combinatoricsSolved

                    this[grobs] = row.grobs
                    this[position] = ranks[row.name()]!!
                    this[date] =
                        Clock.System
                            .now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                }
        }
    }

    fun updateGroupAll(
        names: List<String>,
        group: GroupType,
    ) {
        transaction {
            StudentSeason2.update({ StudentSeason2.name inList names }) {
                it[StudentSeason2.group] = group == GroupType.Urban
            }
        }
    }
}
