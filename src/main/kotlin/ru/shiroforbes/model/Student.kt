package ru.shiroforbes.model

import ru.shiroforbes.database.RatingDAO
import ru.shiroforbes.database.StudentDAO
import ru.shiroforbes.database.WealthDAO

/**
 * Dataclass representing student
 */
class Student(
    val id: Int,
    name: String = "",
    login: String = "",
    password: String = "",
    group: GroupType,
    val rating: Int = 0,
    val wealth: Int = 0,
    val totalSolved: Int = 0,
    val algebraSolved: Int = 0,
    val geometrySolved: Int = 0,
    val combinatoricsSolved: Int = 0,
    val isExercised: Boolean?,
    val isBeaten: Boolean?,
    val isInvesting: Boolean?,
    val wealthHistory: MutableList<Int> = mutableListOf(),
    val ratingHistory: MutableList<Float> = mutableListOf(),
    private val algebraHistory: MutableList<Float> = mutableListOf(),
    private val geometryHistory: MutableList<Float> = mutableListOf(),
    private val combinatoricsHistory: MutableList<Float> = mutableListOf(),
) : User(name, login, password, group, false) {
    constructor(
        dao: StudentDAO,
        ratings: List<RatingDAO>,
        wealths: List<WealthDAO>,
    ) : this(
        dao.id.value,
        dao.name,
        dao.login,
        dao.password,
        dao.group,
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
        wealths.sortedBy { it.date }.map { it.wealth }.forEach { populateWealthHistory(it) }

        ratings.sortedBy { it.date }.map { it.total }.forEach { populateRatingHistory(it) }
        ratings.sortedBy { it.date }.map { it.algebra }.forEach { populateAlgebraHistory(it) }
        ratings.sortedBy { it.date }.map { it.geometry }.forEach { populateGeometryHistory(it) }
        ratings.sortedBy { it.date }.map { it.combinatorics }.forEach { populateCombinatoricsHistory(it) }
    }

    fun firstName(): String = name.split(" ").first()

    fun lastName(): String = name.split(" ").last()

    fun populateWealthHistory(wealth: Int) = wealthHistory.add(wealth)

    fun populateRatingHistory(rating: Float) = ratingHistory.add(rating)

    fun populateAlgebraHistory(algebra: Float) = algebraHistory.add(algebra)

    fun populateGeometryHistory(geometry: Float) = geometryHistory.add(geometry)

    fun populateCombinatoricsHistory(combinatorics: Float) = combinatoricsHistory.add(combinatorics)
}
