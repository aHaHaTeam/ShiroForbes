package ru.shiroforbes.model

class Student(
    val name: String,
    val group: Boolean,
    login: String,
    val score: Int,
    val total: Float,
    val ratings: List<Rating>,
) : User(login, Rights.Student) {
    constructor(stat: StudentStat, ratings: List<Rating>) : this(
        stat.name,
        stat.group,
        stat.login,
        stat.score,
        stat.total,
        ratings,
    )
}
