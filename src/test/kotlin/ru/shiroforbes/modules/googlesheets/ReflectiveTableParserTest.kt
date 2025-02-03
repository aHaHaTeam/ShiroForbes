package ru.shiroforbes.modules.googlesheets

import ru.shiroforbes.model.Group
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ReflectiveTableParserTest {
    data class Structure(
        val int: Int,
        val string: String,
        val float: Float,
        val boolean: Boolean,
        val group: Group?,
        val nullable: Int?,
    )

    @Test
    fun parse() {
        val table = listOf(
            listOf("1", "", "0.5", "true", "Urban", "fake"),
            listOf("-1", "string", "0,9", "fAlSe", "countryside", "42"),
        )
        val expectedResult = listOf(
            Structure(1, "", .5f, true, Group.Urban, null),
            Structure(-1, "string", .9f, false, null, 42),
        )
        val decoders = listOf(CustomDecoder(), DefaultDecoder())
        val actualResult = ReflectiveTableParser(Structure::class, decoders).parse(table)
        assertContentEquals(expectedResult, actualResult)
    }
}