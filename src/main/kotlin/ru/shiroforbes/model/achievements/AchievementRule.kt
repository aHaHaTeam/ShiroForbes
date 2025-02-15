package ru.shiroforbes.model.achievements

interface AchievementRule {
    fun achieved(performance: Performance): Achievement?
}
