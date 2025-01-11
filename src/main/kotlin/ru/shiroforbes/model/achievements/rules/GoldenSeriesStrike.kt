package ru.shiroforbes.model.achievements.rules

import ru.shiroforbes.model.achievements.Achievement
import ru.shiroforbes.model.achievements.AchievementRule
import ru.shiroforbes.model.achievements.Performance
import kotlin.math.max

class GoldenSeriesStrike : AchievementRule {
    override fun achieved(performance: Performance): Achievement? {
        var record = 0
        var last = 0
        var currentStrike = 0
        val goldenSeries =
            performance.problems
                .filter { series -> series.solved.all { it == 1.0 } }
                .map { series -> series.number }
                .sorted()
        for (i in 1..goldenSeries.size) {
            if (goldenSeries[i] == goldenSeries[i - 1] + 1) {
                currentStrike++
            } else {
                if (currentStrike > record) {
                    record = currentStrike
                    last = goldenSeries[i - 1]
                }
                currentStrike = 1
            }
        }
        record = max(record, currentStrike)
        if (record == 0) return null
        return Achievement(
            "Полоса побед",
            "Максимальное количество золотых серий полученных подряд",
            "puscheen.png",
            last,
            record,
        )
    }
}
