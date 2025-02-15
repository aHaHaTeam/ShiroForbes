package ru.shiroforbes.service

import ru.shiroforbes.model.achievements.Achievement

interface AchievementService {
    fun addAchievements(achievements: List<Pair<Int, Achievement>>)
    fun clearAchievements()
    fun getAchievementsOfStudent(studentId: Int): List<Achievement>
}