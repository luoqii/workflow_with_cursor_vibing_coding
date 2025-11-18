package com.example.helloworlddemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GreetingFormatterTest {

    private val formatter = GreetingFormatter()

    @Test
    fun `formatDefaultGreeting capitalizes first letter by default`() {
        val result = formatter.formatDefaultGreeting("hello world")
        assertEquals("Hello world", result)
    }

    @Test
    fun `formatDefaultGreeting trims whitespace`() {
        val result = formatter.formatDefaultGreeting("   hello world   ")
        assertEquals("Hello world", result)
    }

    @Test
    fun `formatDefaultGreeting can keep original casing`() {
        val result = formatter.formatDefaultGreeting("hello world", uppercaseFirstLetter = false)
        assertEquals("hello world", result)
    }

    @Test
    fun `formatDefaultGreeting throws when message is blank`() {
        assertThrows(IllegalArgumentException::class.java) {
            formatter.formatDefaultGreeting("   ")
        }
    }

    @Test
    fun `buildPersonalGreeting uses fallback when empty`() {
        val result = formatter.buildPersonalGreeting("   ")
        assertEquals("Hello, there!", result)
    }

    @Test
    fun `buildPersonalGreeting formats custom name`() {
        val result = formatter.buildPersonalGreeting("Alice")
        assertEquals("Hello, Alice!", result)
    }
}

