@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.service

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.database.RatingTable
import ru.shiroforbes.database.StudentStat
import ru.shiroforbes.database.StudentTable
import ru.shiroforbes.model.Student

class DbStudentService(
    private val database: Database,
    private val ratingService: RatingService,
) : StudentService {
    override fun getStudentStatById(id: Int): StudentStat? =
        transaction(database) {
            val lastDate =
                RatingTable
                    .join(
                        StudentTable,
                        JoinType.INNER,
                        onColumn = RatingTable.student,
                        otherColumn = StudentTable.id,
                    ).select(RatingTable.date.max().alias("maxDate"))
                    .where(RatingTable.student eq StudentTable.id and (StudentTable.id eq id))
                    .firstOrNull() ?: return@transaction null

            return@transaction StudentTable
                .join(
                    RatingTable,
                    JoinType.INNER,
                    onColumn = StudentTable.id,
                    otherColumn = RatingTable.student,
                ).select(
                    StudentTable.login,
                    StudentTable.group,
                    StudentTable.name,
                    RatingTable.points,
                    RatingTable.total,
                ).where(
                    RatingTable.date eq lastDate[RatingTable.date.max().alias("maxDate")]!!,
                ).map {
                    StudentStat(
                        it[StudentTable.name],
                        it[StudentTable.group],
                        it[StudentTable.login],
                        it[RatingTable.points],
                        it[RatingTable.total],
                    )
                }.firstOrNull()
        }

    override fun getAllStudentsByName(): Map<String, StudentStat> =
        transaction(database) {
            val lastDate =
                RatingTable
                    .join(
                        StudentTable,
                        JoinType.INNER,
                        onColumn = RatingTable.student,
                        otherColumn = StudentTable.id,
                    ).select(RatingTable.date.max(), StudentTable.login)
                    .where(RatingTable.student eq StudentTable.id)
                    .groupBy(StudentTable.login)
                    .map { it[RatingTable.date.max()] to it[StudentTable.login] }
                    .filter { it.first != null }
                    .map { it.first!! to it.second }

            return@transaction StudentTable
                .join(
                    RatingTable,
                    JoinType.INNER,
                    onColumn = StudentTable.id,
                    otherColumn = RatingTable.student,
                ).select(
                    StudentTable.login,
                    StudentTable.group,
                    StudentTable.name,
                    RatingTable.points,
                    RatingTable.total,
                ).where(RatingTable.date to StudentTable.login inList lastDate)
                .map {
                    it[StudentTable.name] to
                            StudentStat(
                                it[StudentTable.name],
                                it[StudentTable.group],
                                it[StudentTable.login],
                                it[RatingTable.points],
                                it[RatingTable.total],
                            )
                }.associateBy({ it.first }, { it.second })
        }

    override fun getStudentStatByName(name: String): StudentStat? =
        transaction(database) {
            val lastDate =
                RatingTable
                    .join(
                        StudentTable,
                        JoinType.INNER,
                        onColumn = RatingTable.student,
                        otherColumn = StudentTable.id,
                    ).select(RatingTable.date.max().alias("maxDate"))
                    .where(RatingTable.student eq StudentTable.id and (StudentTable.name eq name))
                    .firstOrNull() ?: return@transaction null


            return@transaction StudentTable
                .join(
                    RatingTable,
                    JoinType.INNER,
                    onColumn = StudentTable.id,
                    otherColumn = RatingTable.student,
                ).select(
                    StudentTable.login,
                    StudentTable.group,
                    StudentTable.name,
                    RatingTable.points,
                    RatingTable.total,
                ).where(RatingTable.date eq lastDate.get(RatingTable.date.max().alias("maxDate"))!!).map {
                    StudentStat(
                        it[StudentTable.name],
                        it[StudentTable.group],
                        it[StudentTable.login],
                        it[RatingTable.points],
                        it[RatingTable.total],
                    )
                }.firstOrNull()
        }

    override fun getStudentStatByLogin(login: String): StudentStat? =
        transaction(database) {
            val lastDate =
                RatingTable
                    .join(
                        StudentTable,
                        JoinType.INNER,
                        onColumn = RatingTable.student,
                        otherColumn = StudentTable.id,
                    ).select(RatingTable.date.max().alias("maxDate"), RatingTable.student.alias("student1"))
                    .where(RatingTable.student eq StudentTable.id and (StudentTable.login eq login))
                    .groupBy(RatingTable.student)
                    .firstOrNull() ?: return@transaction null
            if (lastDate[RatingTable.date.max().alias("maxDate")] == null) {
                return@transaction null
            }

            return@transaction StudentTable
                .join(
                    RatingTable,
                    JoinType.INNER,
                    onColumn = StudentTable.id,
                    otherColumn = RatingTable.student,
                ).select(
                    StudentTable.login,
                    StudentTable.group,
                    StudentTable.name,
                    RatingTable.points,
                    RatingTable.total,
                ).where(
                    RatingTable.date eq lastDate[RatingTable.date.max().alias("maxDate")]!! and
                            (RatingTable.student eq lastDate[RatingTable.student]),
                ).map {
                    StudentStat(
                        it[StudentTable.name],
                        it[StudentTable.group],
                        it[StudentTable.login],
                        it[RatingTable.points],
                        it[RatingTable.total],
                    )
                }.firstOrNull()
        }

    override suspend fun getStudentByLogin(login: String): Student {
        val stats = getStudentStatByLogin(login)
        val ratings = ratingService.getRatings(login)
        return Student(stats!!, ratings)
    }

    override fun getPasswordByLogin(login: String): String? =
        transaction(database) {
            StudentTable
                .select(StudentTable.password)
                .where(StudentTable.login eq login)
                .firstOrNull()
                ?.get(StudentTable.password)
        }
}
