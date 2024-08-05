@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import ru.shiroforbes.config.DbConfig
import ru.shiroforbes.config.GoogleSheetsConfig
import ru.shiroforbes.config.RouterConfig
import ru.shiroforbes.config.configureApp
import ru.shiroforbes.jobs.DailyResetBeat
import ru.shiroforbes.jobs.DailyResetExercise

data class Config(
    val googleSheetsConfig: GoogleSheetsConfig,
    val routerConfig: RouterConfig,
    val dbConfig: DbConfig,
)

internal val config: Config by lazy {
    ConfigLoaderBuilder
        .default()
        .addResourceSource("/config.yaml")
        .build()
        .loadConfigOrThrow<Config>()
}

fun main(args: Array<String>) {
    if (args.contains("--init")) {
        ru.shiroforbes.database.main()
        return
    }
    runBlocking {
        launch {
            val exercise =
                JobBuilder
                    .newJob(DailyResetExercise::class.java)
                    .withIdentity("exercise")
                    .build()

            val morning =
                TriggerBuilder
                    .newTrigger()
                    .withIdentity("morning")
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(7, 0))
                    .build()

            val beat =
                JobBuilder
                    .newJob(DailyResetBeat::class.java)
                    .withIdentity("beat")
                    .build()

            val evening =
                TriggerBuilder
                    .newTrigger()
                    .withIdentity("evening")
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(22, 0))
                    .build()

            val scheduler = StdSchedulerFactory.getDefaultScheduler()
            scheduler.start()
            scheduler.scheduleJob(exercise, morning)
            scheduler.scheduleJob(beat, evening)
        }
        launch {
            embeddedServer(Netty, port = 80, host = "0.0.0.0", module = Application::module)
                .start(wait = true)
        }
    }
}

fun Application.module() {
    configureApp(config)
}
