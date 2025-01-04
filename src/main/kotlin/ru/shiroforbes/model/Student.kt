package ru.shiroforbes.model

import ru.shiroforbes.database.RatingDAO2
import ru.shiroforbes.database.StudentStat

class Student(
    val name: String,
    val group: Boolean,
    login: String,
    val score: Int,
    val total: Float,
    val ratings: List<RatingDAO2>,
) : User(login, false) {
    constructor(stat: StudentStat, ratings: List<RatingDAO2>) : this(
        stat.name,
        stat.group,
        stat.login,
        stat.score,
        stat.total,
        ratings,
    )
}
