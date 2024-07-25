package ru.shiroforbes.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    Database.connect(
        "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?prepareThreshold=0",
        driver = "org.postgresql.Driver",
        user = "postgres.lsbuufukrknwuixafuuu",
        password = "shiroforbes239",
    )

    transaction {
        create(Students, Ratings, Wealths, Transactions, StudentRatings, StudentWealth, StudentTransaction)
        println(StudentDAO.findById(1))
//        Students.insert { student ->
//            student[name] = "Name1"
//            student[login]= "Login1"
//            student[password]="Password1"
//            student[rating] = 121
//            student[wealth] = 1231
//            student[totalSolved] = 0
//            student[algebraSolved] = 0
//            student[geometrySolved] = 0
//            student[combinatoricsSolved] = 0
//            student[isExercised] =null
//            student[isBeaten]=false
//            student[isInvesting]=false
//        }
    }
}
