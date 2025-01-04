@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.service

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.config
import ru.shiroforbes.database.RatingSeason2
import ru.shiroforbes.database.RatingSeason2.date
import ru.shiroforbes.database.RatingSeason2.points
import ru.shiroforbes.database.RatingSeason2.student
import ru.shiroforbes.database.RatingSeason2.total
import ru.shiroforbes.database.StudentSeason2
import ru.shiroforbes.database.StudentStat
import ru.shiroforbes.model.Student

object DbStudentService {
    init {
        Database.connect(
            config.dbConfig.connectionUrl,
            config.dbConfig.driver,
            config.dbConfig.user,
            config.dbConfig.password,
        )
    }

    fun getStudentStatById(id: Int): StudentStat? =
        transaction {
            val lastDate =
                RatingSeason2
                    .join(
                        StudentSeason2,
                        JoinType.INNER,
                        onColumn = RatingSeason2.student,
                        otherColumn = StudentSeason2.id,
                    ).select(RatingSeason2.date.max().alias("maxDate"))
                    .where(RatingSeason2.student eq StudentSeason2.id and (StudentSeason2.id eq id))
                    .firstOrNull()
            if (lastDate == null) {
                return@transaction null
            }

            return@transaction StudentSeason2
                .join(
                    RatingSeason2,
                    JoinType.INNER,
                    onColumn = StudentSeason2.id,
                    otherColumn = RatingSeason2.student,
                ).select(
                    StudentSeason2.login,
                    StudentSeason2.group,
                    StudentSeason2.name,
                    RatingSeason2.points,
                    RatingSeason2.total,
                ).where(
                    RatingSeason2.date eq lastDate.get(RatingSeason2.date.max().alias("maxDate"))!!,
                ).map {
                    StudentStat(
                        it[StudentSeason2.name],
                        it[StudentSeason2.group],
                        it[StudentSeason2.login],
                        it[RatingSeason2.points],
                        it[RatingSeason2.total],
                    )
                }.firstOrNull()
        }

    fun getAllStudentsByName(): Map<String, StudentStat> =
        transaction {
            val lastDate =
                RatingSeason2
                    .join(
                        StudentSeason2,
                        JoinType.INNER,
                        onColumn = RatingSeason2.student,
                        otherColumn = StudentSeason2.id,
                    ).select(RatingSeason2.date.max(), StudentSeason2.login)
                    .where(RatingSeason2.student eq StudentSeason2.id)
                    .groupBy(StudentSeason2.login)
                    .map {
                        return@map (it[RatingSeason2.date.max()] to it[StudentSeason2.login])
                    }.filter { it.first != null }
                    .map { it.first!! to it.second!! }

            return@transaction StudentSeason2
                .join(
                    RatingSeason2,
                    JoinType.INNER,
                    onColumn = StudentSeason2.id,
                    otherColumn = RatingSeason2.student,
                ).select(
                    StudentSeason2.login,
                    StudentSeason2.group,
                    StudentSeason2.name,
                    RatingSeason2.points,
                    RatingSeason2.total,
                ).where(
                    RatingSeason2.date to StudentSeason2.login inList lastDate,
                ).map {
                    it[StudentSeason2.name] to
                        StudentStat(
                            it[StudentSeason2.name],
                            it[StudentSeason2.group],
                            it[StudentSeason2.login],
                            it[RatingSeason2.points],
                            it[RatingSeason2.total],
                        )
                }.associateBy({ it.first }, { it.second })
        }

    fun getStudentStatByName(name: String): StudentStat? =
        transaction {
            val lastDate =
                RatingSeason2
                    .join(
                        StudentSeason2,
                        JoinType.INNER,
                        onColumn = RatingSeason2.student,
                        otherColumn = StudentSeason2.id,
                    ).select(RatingSeason2.date.max().alias("maxDate"))
                    .where(RatingSeason2.student eq StudentSeason2.id and (StudentSeason2.name eq name))
                    .firstOrNull()
            if (lastDate == null) {
                return@transaction null
            }

            return@transaction StudentSeason2
                .join(
                    RatingSeason2,
                    JoinType.INNER,
                    onColumn = StudentSeason2.id,
                    otherColumn = RatingSeason2.student,
                ).select(
                    StudentSeason2.login,
                    StudentSeason2.group,
                    StudentSeason2.name,
                    RatingSeason2.points,
                    RatingSeason2.total,
                ).where(
                    RatingSeason2.date eq lastDate.get(RatingSeason2.date.max().alias("maxDate"))!!,
                ).map {
                    StudentStat(
                        it[StudentSeason2.name],
                        it[StudentSeason2.group],
                        it[StudentSeason2.login],
                        it[RatingSeason2.points],
                        it[RatingSeason2.total],
                    )
                }.firstOrNull()
        }

    fun getStudentStatByLogin(login: String): StudentStat? =
        transaction {
            val lastDate =
                RatingSeason2
                    .join(
                        StudentSeason2,
                        JoinType.INNER,
                        onColumn = RatingSeason2.student,
                        otherColumn = StudentSeason2.id,
                    ).select(RatingSeason2.date.max().alias("maxDate"), student.alias("student1"))
                    .where(RatingSeason2.student eq StudentSeason2.id and (StudentSeason2.login eq login))
                    .groupBy(RatingSeason2.student)
                    .firstOrNull()
            if (lastDate?.get(RatingSeason2.date.max().alias("maxDate")) == null) {
                return@transaction null
            }

            return@transaction StudentSeason2
                .join(
                    RatingSeason2,
                    JoinType.INNER,
                    onColumn = StudentSeason2.id,
                    otherColumn = RatingSeason2.student,
                ).select(
                    StudentSeason2.login,
                    StudentSeason2.group,
                    StudentSeason2.name,
                    RatingSeason2.points,
                    RatingSeason2.total,
                ).where(
                    RatingSeason2.date eq lastDate.get(RatingSeason2.date.max().alias("maxDate"))!! and
                        (RatingSeason2.student eq lastDate.get(RatingSeason2.student!!)),
                ).map {
                    StudentStat(
                        it[StudentSeason2.name],
                        it[StudentSeason2.group],
                        it[StudentSeason2.login],
                        it[RatingSeason2.points],
                        it[RatingSeason2.total],
                    )
                }.firstOrNull()
        }

    suspend fun getStudentByLogin(login: String): Student {
        val stats = getStudentStatByLogin(login)
        val ratings = DbRatingService.getRatings(login)

        return Student(stats!!, ratings)
    }

    fun getPasswordByLogin(login: String): String? =
        transaction {
            StudentSeason2
                .select(StudentSeason2.password)
                .where(StudentSeason2.login eq login)
                .firstOrNull()
                ?.get(StudentSeason2.password)
        }
}
