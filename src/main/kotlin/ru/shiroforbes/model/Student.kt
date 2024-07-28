package ru.shiroforbes.model

import ru.shiroforbes.database.RatingDAO
import ru.shiroforbes.database.StudentDAO
import ru.shiroforbes.database.WealthDAO

/**
 * Dataclass representing student
 */
class Student(
    val id: Int,
    val name: String = "",
    val login: String = "",
    val password: String = "",
    val rating: Int = 0,
    val wealth: Int = 0,
    val totalSolved: Int = 0,
    val algebraSolved: Int = 0,
    val geometrySolved: Int = 0,
    val combinatoricsSolved: Int = 0,

    private var isExercised_: Boolean?,
    private var isBeaten_: Boolean?,
    private var isInvesting_: Boolean?,
    var wealthHistory: List<Int> = listOf(),
    var ratingHistory: List<Float> = listOf(),
    var algebraHistory: List<Float> = listOf(),
    var geometryHistory: List<Float> = listOf(),
    var combinatoricsHistory: List<Float> = listOf(),
) {
    constructor(
        dao: StudentDAO,
        ratings: List<RatingDAO>,
        wealths: List<WealthDAO>,
    ) : this(
        dao.id.value,
        dao.name,
        dao.login,
        dao.password,
        dao.rating,
        dao.wealth,
        dao.totalSolved,
        dao.algebraSolved,
        dao.geometrySolved,
        dao.combinatoricsSolved,
        dao.isExercised,
        dao.isBeaten,
        dao.isInvesting,
    ) {
        wealthHistory = wealths.sortedBy { it.date }.map { it.wealth }

        ratingHistory = ratings.sortedBy { it.date }.map { it.total }
        algebraHistory = ratings.sortedBy { it.date }.map { it.algebra }
        geometryHistory = ratings.sortedBy { it.date }.map { it.geometry }
        combinatoricsHistory = ratings.sortedBy { it.date }.map { it.combinatorics }
    }

    var isExercised: Boolean?
        get() = isExercised_
        set(value) {
            TODO()
        }
    var isBeaten: Boolean?
        get() = isBeaten_
        set(value) {
            TODO()
        }

    var isInvesting: Boolean?
        get() = isInvesting_
        set(value) {
            TODO()
        }

    fun firstName(): String = name.split(" ").first()

    fun lastName(): String = name.split(" ").last()
}
