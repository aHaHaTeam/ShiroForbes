@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import ru.shiroforbes.config.RouterConfig
import ru.shiroforbes.login.Session
import ru.shiroforbes.login.isAdmin
import ru.shiroforbes.login.validUser
import ru.shiroforbes.model.*
import ru.shiroforbes.modules.googlesheets.RatingDeserializer
import ru.shiroforbes.modules.markdown.MarkdownConverter
import ru.shiroforbes.modules.serialization.RatingSerializer
import ru.shiroforbes.service.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDateTime as JavaDateTime

@OptIn(FormatStringsInDatetimeFormats::class)
fun Routing.routes(
    studentService: StudentService? = null,
    eventService: EventService? = null,
    transactionService: TransactionService,
    ratingSerializer: RatingSerializer,
    ratingDeserializer: RatingDeserializer,
    markdownConverter: MarkdownConverter,
    routerConfig: RouterConfig,
) {
    staticFiles("/static", File("src/main/resources/static/"))

    get("/favicon.ico") {
        call.respondFile(File("src/main/resources/static/images/shiro&vlasik.png"))
    }

    get("/grobarium") {
        call.respondRedirect(routerConfig.grobariumUrl)
    }

    get("/series") {
        call.respondRedirect(routerConfig.seriesUrl)
    }

    get("/lz") {
        call.respondRedirect(routerConfig.lzUrl)
    }

    authenticate("auth-session-no-redirect") {
        get("/menu") {
            var user: Any = 0
            if (call.principal<Session>() != null) {
                if (call.principal<Session>()!!.login != "") {
                    user = DbUserService.getUserByLogin(call.principal<Session>()?.login!!) ?: 0
                }
            }

            val events = eventService!!.getAllEvents()
            call.respond(
                ThymeleafContent(
                    "menu",
                    mapOf(
                        "countrysideCampStudents" to
                            studentService!!
                                .getGroup(GroupType.Countryside)
                                .sortedByDescending { it.rating + it.wealth },
                        "urbanCampStudents" to
                            studentService
                                .getGroup(GroupType.Urban)
                                .sortedByDescending { it.rating + it.wealth },
                        "user" to user,
                        "countrysideCampEvents" to events.filter { it.group == GroupType.Countryside },
                        "urbanCampEvents" to events.filter { it.group == GroupType.Urban },
                    ),
                ),
            )
        }
    }

    get("/login") {
        call.respond(
            ThymeleafContent(
                "login",
                mapOf(),
            ),
        )
    }

    post("/login") {
        val formContent = call.receiveText()
        val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()
        val name = params.jsonValue("login")
        val password = params.jsonValue("password")
        if (!validUser(name, password)) {
            return@post
        }
        call.sessions.set(Session(name, password))
        if (isAdmin(name)) {
            call.respondRedirect("/menu")
            return@post
        }
        val user = DbUserService.getUserByLogin(name)
        call.respondRedirect("/profile/${user!!.login}")
    }

    authenticate("auth-session") {
        get("/admin") {
            if (!isAdmin(call.principal<Session>()?.login)) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                call.respond(
                    ThymeleafContent(
                        "components/rating",
                        mapOf("students" to studentService!!.getGroup(GroupType.Countryside)),
                    ),
                )
            }
        }
    }

    authenticate("auth-session-no-redirect") {
        get("/profile/{login}") {
            var activeUser: Any = 0
            if (call.principal<Session>() != null) {
                if (call.principal<Session>()!!.login != "") {
                    activeUser = DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0
                }
            }
            val user = DbUserService.getUserByLogin(call.parameters["login"]!!)!!
            if (!user.HasAdminRights) {
                user as Student
                val transactions =
                    DbTransactionService
                        .getAllStudentTransactions(user.id)
                        .filter { it.date.toJavaLocalDateTime().isBefore(JavaDateTime.now()) }
                        .sortedByDescending { it.date }
                        .map {
                            TransactionUtil(
                                it.id,
                                user,
                                it.size,
                                it.date.format(LocalDateTime.Format { byUnicodePattern("HH:mm:ss") }),
                                it.date.format(LocalDateTime.Format { byUnicodePattern("dd.MM.yyyy") }),
                                it.description,
                            )
                        }
                call.respond(
                    ThymeleafContent(
                        "profile",
                        mapOf(
                            "investingAllowed" to false,
                            "user" to user,
                            "rating" to user.ratingHistory,
                            "wealth" to user.wealthHistory,
                            "activeUser" to activeUser,
                            "transactions" to transactions,
                        ),
                    ),
                )
            } else {
                call.respondRedirect("/menu")
            }
        }
    }

    authenticate("auth-session-no-redirect") {
        get("/marketplace") {
            var activeUser: Any = 0
            if (call.principal<Session>() != null) {
                if (call.principal<Session>()!!.login != "") {
                    activeUser = DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0
                }
            }
            call.respond(
                ThymeleafContent(
                    "marketplace",
                    mapOf(
                        "user" to activeUser,
                        "offers" to
                            DbOfferService.getAllOffers().sortedBy { it.price },
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        post("/marketplace") {
            val formContent = call.receiveText()
            val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()
            println(params)
            if (params.jsonValue("action") == "add") {
                val offer =
                    Offer(
                        id = -1,
                        name = params.jsonValue("offerName"),
                        description = params.jsonValue("offerDescription"),
                        price = params.jsonValue("offerPrice").toInt(),
                    )
                println(offer.name)
                println(offer.description)
                println(offer.price)
                DbOfferService.addOffer(offer)
            }

            if (params.jsonValue("action") == "remove") {
                val offerId = params.jsonValue("id").toInt()
                println(offerId)
                DbOfferService.deleteOffer(offerId)
            }
            call.respondRedirect("/marketplace")
        }
    }

    get("/logout") {
        call.sessions.set(Session("", ""))
        call.respondRedirect("/login")
    }

    authenticate("auth-session") {
        get("/mock/admin") {
            call.respond(
                ThymeleafContent(
                    "admin",
                    mapOf("students" to studentService!!.getGroup(GroupType.Countryside)),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        get("/update/rating") {
            val countrysideDeltas =
                computeRatingDeltas(
                    studentService!!.getGroup(GroupType.Countryside),
                    ratingDeserializer.getCountrysideRating(),
                )
            val urbanDeltas =
                computeRatingDeltas(
                    studentService.getGroup(GroupType.Urban),
                    ratingDeserializer.getUrbanRating(),
                )

            call.respond(
                ThymeleafContent(
                    "update_rating",
                    mapOf(
                        "user" to (DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                        "countrysideStudents" to countrysideDeltas,
                        "urbanStudents" to urbanDeltas,
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        post("/update/countryside/rating") {
            updateRating(studentService!!, ratingDeserializer.getCountrysideRating())
            call.respondRedirect("/update/rating")
        }
    }

    authenticate("auth-session-admin-only") {
        post("/update/urban/rating") {
            updateRating(studentService!!, ratingDeserializer.getUrbanRating())
            call.respondRedirect("/update/rating")
        }
    }

    get("/download/urban/rating.pdf") {
        val outputStream = ByteArrayOutputStream()
        ratingSerializer.serialize(outputStream, studentService!!.getGroup(GroupType.Urban))
        call.respondBytes(outputStream.toByteArray())
    }

    get("/download/countryside/rating.pdf") {
        val outputStream = ByteArrayOutputStream()
        ratingSerializer.serialize(outputStream, studentService!!.getGroup(GroupType.Countryside))
        call.respondBytes(outputStream.toByteArray())
    }

    authenticate("auth-session-admin-only") {
        get("/transactions/exercises/countryside") {
            val countryside = studentService!!.getGroup(GroupType.Countryside).sortedBy { student -> student.name }
            call.respond(
                ThymeleafContent(
                    "exercises",
                    mapOf(
                        "students" to countryside,
                        "user" to (DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        post("/transactions/exercises/countryside") {
            val formContent = call.receiveText()
            val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()

            val transactionDescription =
                when (params.jsonValue("activityType")) {
                    "exercises" -> "Зарядка"
                    "cleaning" -> "Уборка палаты"
                    "promenade" -> "Случайный момент"
                    "curfew" -> "Успешный отбой"
                    else -> params.jsonValue("activityType")
                }

            val transactionAmount =
                when (params.jsonValue("activityType")) {
                    "exercises" -> 40
                    "cleaning" -> 40
                    "promenade" -> 50
                    "curfew" -> 20
                    else -> 0
                }

            val transactionDateTime = LocalDateTime.parse(params.jsonValue("dateTime"))

            val students =
                params.keys
                    .mapNotNull {
                        if (it == "activityType" || it == "dateTime") {
                            null
                        } else {
                            if (params[it].toString() == "true") {
                                it.toInt()
                            } else {
                                null
                            }
                        }
                    }.toSet()

            transactionService.sendMoneyByCondition(
                studentService!!.getAllStudents(),
                transactionAmount,
                transactionDateTime,
                transactionDescription,
            ) {
                this.id in students
            }

            call.respondRedirect("/menu")
        }
    }

    authenticate("auth-session-admin-only") {
        get("/transactions/transactions/countryside") {
            val countryside = studentService!!.getGroup(GroupType.Countryside).sortedBy { student -> student.name }
            call.respond(
                ThymeleafContent(
                    "transactions",
                    mapOf(
                        "students" to countryside,
                        "user" to (DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        get("/transactions/transactions/urban") {
            val urban = studentService!!.getGroup(GroupType.Urban).sortedBy { student -> student.name }
            call.respond(
                ThymeleafContent(
                    "transactions",
                    mapOf(
                        "students" to urban,
                        "user" to (DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        post("/transactions/transactions") {
            val formContent = call.receiveText()
            val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()
            println(params)
            val transactionName = params.jsonValue("transactionName")
            val transactionDateTime = LocalDateTime.parse(params.jsonValue("dateTime"))
            for (login: String in params.keys) {
                if (login == "transactionName" || login == "dateTime") {
                    continue
                }
                val size = params.jsonValue(login).toInt()
                val studentId = DbStudentService.getStudentByLogin(login)!!.id
                DbTransactionService.makeTransaction(
                    Transaction(
                        id = 0,
                        studentId = studentId,
                        size = size,
                        date = transactionDateTime,
                        description = transactionName,
                    ),
                )
            }

            call.respondRedirect("/menu")
        }
    }

    authenticate("auth-session-admin-only") {
        post("/transactions/delete/{id}") {
            val transaction = transactionService.getTransaction(call.parameters["id"]!!.toInt())!!
            val student = studentService!!.getStudentById(transaction.studentId)!!
            studentService.updateWealth(student.id, student.wealth - transaction.size)
            transactionService.deleteTransaction(call.parameters["id"]!!.toInt())
            call.respond(HttpStatusCode.Accepted)
        }
    }

    authenticate("auth-session-admin-only") {
        get("/transactions/history") {
            val students = studentService!!.getAllStudents().associateBy { it.id }
            val transactions =
                transactionService
                    .getAllTransactions()
                    .sortedByDescending { it.date }
                    .map {
                        TransactionUtil(
                            it.id,
                            students[it.studentId]!!,
                            it.size,
                            it.date.format(LocalDateTime.Format { byUnicodePattern("HH:mm") }),
                            it.date.format(LocalDateTime.Format { byUnicodePattern("dd.MM.yyyy") }),
                            it.description,
                        )
                    }.groupBy { it.student.group }
            call.respond(
                ThymeleafContent(
                    "transactions_history",
                    mapOf(
                        "urbanTransactions" to (transactions[GroupType.Urban] ?: listOf()),
                        "countrysideTransactions" to (transactions[GroupType.Countryside] ?: listOf()),
                        "user" to (DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                    ),
                ),
            )
        }
    }

    get("/") {
        call.respondRedirect("/menu")
    }

    authenticate("auth-session-admin-only") {
        get("/event/new") {
            val user = DbUserService.getUserByLogin(call.principal<Session>()!!.login)!!
            call.respond(
                ThymeleafContent(
                    "event_editing",
                    mapOf(
                        "user" to user,
                        "event" to Event(1, user.group, "", "", ""),
                        "isCreation" to true,
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        post("/event/new") {
            val formContent = call.receiveText()
            val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()
            val event =
                Event(
                    -1,
                    GroupType.entries.find { it.text == params.jsonValue("group") }!!,
                    params.jsonValue("eventName"),
                    params.jsonValue("timeAndPlace"),
                    markdownConverter.convert(
                        params
                            .jsonValue("eventDescription")
                            .split("\\n")
                            .joinToString("\n"),
                    ),
                )

            eventService!!.addEvent(event)
            call.respondRedirect("/menu")
        }
    }

    authenticate("auth-session-admin-only") {
        get("/event/edit/{id}") {
            val event = eventService!!.getEvent(call.parameters["id"]!!.toInt())!!
            call.respond(
                ThymeleafContent(
                    "event_editing",
                    mapOf(
                        "user" to (DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                        "event" to event,
                        "descriptionRows" to event.description.count { it == '\n' },
                        "isCreation" to false,
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        post("/event/edit/{id}") {
            val formContent = call.receiveText()
            val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()
            println(formContent)
            val event =
                Event(
                    call.parameters["id"]!!.toInt(),
                    GroupType.entries.find { it.text == params.jsonValue("group") }!!,
                    params.jsonValue("eventName"),
                    params.jsonValue("timeAndPlace"),
                    markdownConverter.convert(
                        params
                            .jsonValue("eventDescription")
                            .split("\\n")
                            .joinToString("\n"),
                    ),
                )

            eventService!!.addEvent(event)
            call.respondRedirect("/menu")
        }
    }

    authenticate("auth-session-no-redirect") {
        get("/event/{id}") {
            var user: Any = 0
            if (call.principal<Session>() != null) {
                if (call.principal<Session>()!!.login != "") {
                    user = DbUserService.getUserByLogin(call.principal<Session>()?.login!!) ?: 0
                }
            }
            call.respond(
                ThymeleafContent(
                    "event_viewing",
                    mapOf(
                        "user" to user,
                        "event" to
                            eventService!!.getEvent(call.parameters["id"]!!.toInt())!!,
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        post("/convert/markdown") {
            val data = call.receiveText()
            call.respondText { markdownConverter.convert(data) }
        }
    }

    authenticate("auth-session") {
        post("/profile/investing") {
            val formContent = call.receiveText()
            val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()

            val id = params["userId"].toString().toInt()
            val login = params.jsonValue("login")
            if (login != call.principal<Session>()?.login) {
                call.respond(HttpStatusCode.Unauthorized)
            }
            val investing = params["isInvesting"].toString()
            if (investing == "true") {
                studentService?.updateStudentInvesting(id, true)
            }
            if (investing == "false") {
                studentService?.updateStudentInvesting(id, false)
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun Map<String, JsonElement>.jsonValue(key: String): String {
    val quoted = this[key].toString()
    return quoted.substring(1, quoted.length - 1)
}
