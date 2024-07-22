package ru.shiroforbes.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.shiroforbes.database.Students

// Dataclass representing student
class Student {


    fun solved(): Int {
        return this.algSolved() + this.geomSolved() + this.combSolved()
    }

    fun algSolved(): Int {
        return 1
    }

    fun geomSolved(): Int {
        return 2
    }

    fun combSolved(): Int {
        return 3
    }
}
