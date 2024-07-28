package ru.shiroforbes.model

enum class GroupType {
    CountrysideCamp,
    UrbanCamp,
}

/**
 * Dataclass representing separate group of students, e.g. city camp or regular camp
 */
data class Group(
    val id: Int,
    val name: String,
    val students: List<Student>,
) {
    override fun equals(other: Any?): Boolean = other is Group && this.id == other.id

    override fun hashCode() = id

    override fun toString() = "Group $name"
}
