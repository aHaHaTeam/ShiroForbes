package ru.shiroforbes.model

import ru.shiroforbes.database.StudentDAO2

// represents student or admin
open class User(
    val name: String,
    val login: String,
    val password: String,
    val hasAdminRights: Boolean,
) {
    constructor(studentDAO2: StudentDAO2) : this(
        studentDAO2.name,
        studentDAO2.login,
        studentDAO2.password,
        hasAdminRights = false,
    ) {
    }

    override fun equals(other: Any?): Boolean =
        when (other) {
            is User -> (this.login == other.login && this.password == other.password)
            is Int -> other != 0
            else -> false
        }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
