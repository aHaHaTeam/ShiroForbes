package ru.shiroforbes.model

/**
 * Value representing an event
 *
 * @property [description] The description of the event (rules, comments, external links),
 * represented as an html-formatted string.
 */
data class Event(
    val id: Int,
    val name: String,
    val timeAndPlace: String,
    val description: String,
)
