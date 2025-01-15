package ru.shiroforbes.service

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.database.RatingTable
import ru.shiroforbes.database.StudentTable
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.model.Rating
import ru.shiroforbes.model.Semester
import ru.shiroforbes.modules.googlesheets.RatingRow

class DbRatingService(
    private val database: Database,
) : RatingService {
    override fun getRatings(login: String): Map<Semester, List<Rating>> =
        transaction(database) {
            RatingTable
                .join(StudentTable, JoinType.INNER, RatingTable.student, StudentTable.id)
                .selectAll()
                .where(StudentTable.login eq login)
                .orderBy(RatingTable.date)
                .map { resultRow ->
                    Pair(
                        resultRow[RatingTable.semester],
                        Rating(
                            resultRow[RatingTable.date],
                            resultRow[RatingTable.student],
                            resultRow[RatingTable.points],
                            resultRow[RatingTable.total],
                            resultRow[RatingTable.algebra],
                            resultRow[RatingTable.numbersTheory],
                            resultRow[RatingTable.geometry],
                            resultRow[RatingTable.combinatorics],
                            resultRow[RatingTable.totalPercent],
                            resultRow[RatingTable.algebraPercent],
                            resultRow[RatingTable.numbersTheoryPercent],
                            resultRow[RatingTable.geometryPercent],
                            resultRow[RatingTable.combinatoricsPercent],
                            resultRow[RatingTable.grobs],
                            resultRow[RatingTable.position],
                        ),
                    )
                }
        }.groupBy({ it.first }, { it.second })
            .mapValues { it.value.sortedByDescending { rating -> rating.date } }

    override suspend fun updateRating(
        list: List<RatingRow>,
        semester: Semester,
    ) {
        transaction(database) {
            val ids =
                StudentTable
                    .select(StudentTable.id, StudentTable.name)
                    .associateBy({ it[StudentTable.name] }, { it[StudentTable.id] })
            val ranks =
                list
                    .sortedByDescending { it.rating }
                    .mapIndexed { i, row -> row.name() to i + 1 }
                    .associateBy({ it.first }, { it.second })
            RatingTable
                .batchInsert(list) { row ->
                    this[RatingTable.semester] = semester
                    this[RatingTable.student] = ids[row.name()]!!.value
                    this[RatingTable.total] = row.solvedProblems
                    this[RatingTable.points] = row.rating
                    this[RatingTable.algebraPercent] = row.algebraPercentage
                    this[RatingTable.numbersTheoryPercent] = row.numbersTheoryPercentage
                    this[RatingTable.combinatoricsPercent] = row.combinatoricsPercentage
                    this[RatingTable.geometryPercent] = row.geometryPercentage

                    this[RatingTable.totalPercent] = row.solvedPercentage.toInt() // TODO
                    this[RatingTable.algebra] = row.algebraSolved
                    this[RatingTable.numbersTheory] = row.numbersTheorySolved
                    this[RatingTable.geometry] = row.geometrySolved
                    this[RatingTable.combinatorics] = row.combinatoricsSolved

                    this[RatingTable.grobs] = row.grobs
                    this[RatingTable.position] = ranks[row.name()]!!
                    this[RatingTable.date] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                }
        }
    }

    override fun updateGroup(
        names: List<String>,
        group: GroupType,
    ) {
        transaction(database) {
            StudentTable.update({ StudentTable.name inList names }) {
                it[StudentTable.group] = group
            }
        }
    }
}
