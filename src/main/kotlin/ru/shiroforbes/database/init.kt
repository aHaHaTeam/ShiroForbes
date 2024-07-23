package ru.shiroforbes.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    Database.connect(
        "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres",
        driver = "org.postgresql.Driver",
        user = "postgres.lsbuufukrknwuixafuuu",
        password = "shiroforbes239",
    )

    transaction {
        create(Students, Ratings, Wealths, Transactions, StudentRatings, StudentWealth, StudentTransaction)
    }
}
