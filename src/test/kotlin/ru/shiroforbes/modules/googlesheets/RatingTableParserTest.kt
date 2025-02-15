package ru.shiroforbes.modules.googlesheets

import ru.shiroforbes.model.achievements.Series
import kotlin.test.Test
import kotlin.test.assertContentEquals

class RatingTableParserTest {

    @Test
    fun `parse 0 series`() {
        val ratingTable = listOf(
            listOf("", ""),
            listOf("", "1"),
            listOf("1", ""),
        )
        val expectedRating: List<List<Series>> = listOf(
            listOf(),
            listOf(),
        )
        val actualRating = RatingTableParser().parse(ratingTable)
        assertContentEquals(expectedRating, actualRating)
    }

    @Test
    fun `parse 2 series`() {
        val ratingTable = listOf(
            listOf("", "1", "1", "1", "", "2", "2", ""),
            listOf("", "0", "0,5", "", "", "0.8", "1", ""),
            listOf("", "1", "", "1", "", "0,7", "1", ""),
        )
        val expectedRating = listOf(
            listOf(Series(1, listOf(0f, .5f, 0f)), Series(2, listOf(.8f, 1f))),
            listOf(Series(1, listOf(1f, 0f, 1f)), Series(2, listOf(.7f, 1f))),
        )
        val actualRating = RatingTableParser().parse(ratingTable)
        assertContentEquals(expectedRating, actualRating)
    }
}