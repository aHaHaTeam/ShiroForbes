package ru.shiroforbes.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
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
        create(Students, Ratings, Wealths, Transactions, StudentRatings, StudentWealth, StudentTransaction, Events, Admins)
//        println(StudentDAO.findById(1))
//        Students.insert { student ->
//            student[name] = "Name3"
//            student[login] = "Login3"
//            student[password] = "Pass3"
//            student[group] = GroupType.Urban
//            student[rating] = 2439
//            student[wealth] = 23939
//            student[totalSolved] = 3
//            student[algebraSolved] = 15
//            student[geometrySolved] = 0
//            student[combinatoricsSolved] = 2
//            student[isExercised] = false
//            student[isBeaten] = true
//            student[isInvesting] = false
//        }
//
        Admins.insert {
            it[name] = "vasya"
            it[login] = "vasya566"
            it[password] = "pass123"
            it[group] = GroupType.Urban
        }
    }
}
