package ru.shiroforbes.model

// Dataclass representing student
data class Student(
    val id: Int,
    val name: String,
    val login: String,
    val password: String,
    val rating: Int,
    val wealth: Int,
    val isExercised: Boolean?,
    val isBeaten: Boolean?,
    val isInvesting: Boolean?,
) {
    fun solved(): Int = this.algSolved() + this.geomSolved() + this.combSolved()

    fun algSolved(): Int = 1

    fun geomSolved(): Int = 2

    fun combSolved(): Int = 3
}
