package ru.shiroforbes.modules.googlesheets

import ru.shiroforbes.model.Group
import kotlin.reflect.full.starProjectedType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DefaultDecoderTest {

    @Test
    fun `supports Float, Int, String`() {
        val decoder = DefaultDecoder()
        assert(decoder.supports(Float::class.starProjectedType))
        assert(decoder.supports(Int::class.starProjectedType))
        assert(decoder.supports(String::class.starProjectedType))
    }

    @Test
    fun `doesn't support Boolean, Group`() {
        val decoder = DefaultDecoder()
        assert(!decoder.supports(Boolean::class.starProjectedType))
        assert(!decoder.supports(Group::class.starProjectedType))
    }

    @Test
    fun `convert Float`() {
        val decoder = DefaultDecoder()
        val kType = Float::class.starProjectedType
        assertEquals(.8f, decoder.convert(".8", kType))
        assertEquals(1f, decoder.convert("1", kType))
        assertEquals(1.1f, decoder.convert("1.1", kType))

        assertEquals(null, decoder.convert("0,5", kType))
        assertEquals(null, decoder.convert("1.1.1", kType))
        assertEquals(null, decoder.convert("string", kType))
    }

    @Test
    fun `convert Int`() {
        val decoder = DefaultDecoder()
        val kType = Int::class.starProjectedType
        assertEquals(1, decoder.convert("1", kType))
        assertEquals(-1, decoder.convert("-1", kType))
        assertEquals(0, decoder.convert("-0", kType))
        assertEquals(-1, decoder.convert("-01", kType))

        assertEquals(null, decoder.convert("fake", kType))
        assertEquals(null, decoder.convert("", kType))
    }

    @Test
    fun `convert String`() {
        val decoder = DefaultDecoder()
        val kType = String::class.starProjectedType
        assertEquals("", decoder.convert("", kType))
        assertEquals("string", decoder.convert("string", kType))
    }

    @Test
    fun `convert unsupported`() {
        val decoder = DefaultDecoder()
        val exceptionKClass = IllegalArgumentException::class

        val kTypeBoolean = Boolean::class.starProjectedType
        assertFailsWith(exceptionKClass) { decoder.convert("true", kTypeBoolean) }
        assertFailsWith(exceptionKClass) { decoder.convert("True", kTypeBoolean) }

        val kTypeGroup = Group::class.starProjectedType
        assertFailsWith(exceptionKClass) { decoder.convert("Urban", kTypeGroup) }
        assertFailsWith(exceptionKClass) { decoder.convert("countryside", kTypeGroup) }
    }
}