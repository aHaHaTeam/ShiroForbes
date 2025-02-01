package ru.shiroforbes.modules.googlesheets

import ru.shiroforbes.model.Group
import kotlin.reflect.full.starProjectedType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CustomDecoderTest {

    @Test
    fun `supports Float, Group, Boolean`() {
        val decoder = CustomDecoder()
        assert(decoder.supports(Float::class.starProjectedType))
        assert(decoder.supports(Group::class.starProjectedType))
        assert(decoder.supports(Boolean::class.starProjectedType))
    }

    @Test
    fun `doesn't support String and Int`() {
        val decoder = CustomDecoder()
        assert(!decoder.supports(String::class.starProjectedType))
        assert(!decoder.supports(Int::class.starProjectedType))
    }

    @Test
    fun `convert Float`() {
        val decoder = CustomDecoder()
        val kType = Float::class.starProjectedType
        assertEquals(.8f, decoder.convert(".8", kType))
        assertEquals(.9f, decoder.convert(",9", kType))
        assertEquals(1f, decoder.convert("1", kType))
        assertEquals(1.1f, decoder.convert("1.1", kType))
        assertEquals(1.2f, decoder.convert("1,2", kType))

        assertEquals(null, decoder.convert("", kType))
        assertEquals(null, decoder.convert("1.1.1", kType))
        assertEquals(null, decoder.convert("string", kType))
    }

    @Test
    fun `convert Group`() {
        val decoder = CustomDecoder()
        val kType = Group::class.starProjectedType
        assertEquals(Group.Urban, decoder.convert("Urban", kType))
        assertEquals(Group.Countryside, decoder.convert("Countryside", kType))

        assertEquals(null, decoder.convert("", kType))
        assertEquals(null, decoder.convert("urban", kType))
        assertEquals(null, decoder.convert("fake", kType))
    }

    @Test
    fun `convert Boolean`() {
        val decoder = CustomDecoder()
        val kType = Boolean::class.starProjectedType
        assertEquals(true, decoder.convert("TrUe", kType))
        assertEquals(true, decoder.convert("1", kType))

        assertEquals(false, decoder.convert("fAlSe", kType))
        assertEquals(false, decoder.convert("0", kType))

        assertEquals(null, decoder.convert("", kType))
        assertEquals(null, decoder.convert("fake", kType))
    }

    @Test
    fun `convert unsupported`() {
        val decoder = CustomDecoder()
        val exceptionKClass = IllegalArgumentException::class

        val kTypeInt = Int::class.starProjectedType
        assertFailsWith(exceptionKClass) { decoder.convert("", kTypeInt) }
        assertFailsWith(exceptionKClass) { decoder.convert("1", kTypeInt) }

        val kTypeString = String::class.starProjectedType
        assertFailsWith(exceptionKClass) { decoder.convert("", kTypeString) }
        assertFailsWith(exceptionKClass) { decoder.convert("string", kTypeString) }
    }
}