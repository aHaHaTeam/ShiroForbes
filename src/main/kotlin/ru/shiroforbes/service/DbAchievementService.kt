package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.database.AchievementTable
import ru.shiroforbes.model.achievements.Achievement

class DbAchievementService(private val database: Database) : AchievementService {
    override fun addAchievements(achievements: List<Pair<Int, Achievement>>) {
        transaction(database) {
            AchievementTable.batchInsert(achievements) { (studentId, achievement) ->
                this[AchievementTable.studentId] = studentId
                this[AchievementTable.title] = achievement.title
                this[AchievementTable.description] = achievement.description
                this[AchievementTable.icon] = achievement.icon
                this[AchievementTable.classNumber] = achievement.classNumber
                this[AchievementTable.record] = achievement.record
            }
        }
    }

    override fun clearAchievements() {
        transaction(database) {
            AchievementTable.deleteAll()
        }
    }

    override fun getAchievementsOfStudent(studentId: Int): List<Achievement> =
        transaction(database) {
            AchievementTable.selectAll()
                .where { AchievementTable.studentId eq studentId }
                .map {
                    Achievement(
                        it[AchievementTable.title],
                        it[AchievementTable.description],
                        it[AchievementTable.icon],
                        it[AchievementTable.classNumber],
                        it[AchievementTable.record],
                    )
                }
        }
}