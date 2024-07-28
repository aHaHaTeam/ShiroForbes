package ru.shiroforbes.model

/**
 * All possible types of events.
 * It might be useful in case of filtering events or transactions.
 */
enum class EventType {
    MORNING_EXERCISES,
    COMPETITION,
    INVESTMENT,
}

/**
 * Value representing an event -- a list of transactions
 */
data class Event(
    val id: Int,
    val type: EventType,
    val transactions: List<Transaction>,
    val description: String,
)
