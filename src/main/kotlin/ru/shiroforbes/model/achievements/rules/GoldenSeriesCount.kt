package ru.shiroforbes.model.achievements.rules

import ru.shiroforbes.model.achievements.Achievement
import ru.shiroforbes.model.achievements.AchievementRule
import ru.shiroforbes.model.achievements.Performance
import ru.shiroforbes.model.achievements.Series

class GoldenSeriesCount : AchievementRule {
    override fun achieved(performance: Performance): Achievement? {
        performance.problems.filter { series: Series -> series.solved.all { it == 1.0 } }.also {
            val count = it.size
            val last = it.maxBy { series -> series.number }.number
            if (count == 0) return null
            return Achievement(
                "Больше золота!",
                "Сколько было золотых серий за весь сезон",
                "puscheen.png",
                last,
                count,
            )
        }
    }
}
