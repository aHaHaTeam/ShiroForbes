package ru.shiroforbes.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.model.GroupType

fun main() {
    Database.connect(
        "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?prepareThreshold=0",
        driver = "org.postgresql.Driver",
        user = "postgres.lsbuufukrknwuixafuuu",
        password = "shiroforbes239",
    )

    transaction {
        create(Students, Ratings, Wealths, Transactions, StudentRatings, StudentWealth, StudentTransaction, Events)
        Students.deleteAll()
        for (i in 1..30) {
            Students.insert { student ->
                student[name] = "Name Mane$i"
                student[login] = "Login$i"
                student[password] = "Password$i"
                student[group] = if (i % 2 == 0) GroupType.Countryside else GroupType.Urban
                student[rating] = 721 + 13 * i - i * i
                student[wealth] = 1231 + 2 * i - i * i
                student[totalSolved] = 0
                student[algebraSolved] = 0
                student[geometrySolved] = 0
                student[combinatoricsSolved] = 0
                student[isExercised] = null
                student[isBeaten] = true
                student[isInvesting] = false
            }
        }

        Admins.insert {
            it[name] = "vasya"
            it[login] = "vasya566"
            it[password] = "pass123"
            it[group] = GroupType.Urban
        }
    }
}
