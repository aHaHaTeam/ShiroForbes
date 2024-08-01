package ru.shiroforbes.model

import ru.shiroforbes.database.GroupType

// represents student or admin
open class User(
    val name: String,
    val login: String,
    val password: String,
    val group: GroupType,
    val HasAdminRights: Boolean,
) {
    override fun equals(other: Any?): Boolean =
        when (other) {
            is User -> (this.login == other.login && this.password == other.password)
            is Int -> other == 0
            else -> false
        }
}
