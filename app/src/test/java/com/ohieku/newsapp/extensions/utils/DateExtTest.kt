package com.ohieku.newsapp.extensions.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DateExtTest {

    // 2026-10-13T19:06:00Z
    private val expected = 1791918360000L

    @Test
    fun `parses utc timestamp`() {
        assertEquals(expected, parsePublishedAt("2026-10-13T19:06:00Z"))
    }

    @Test
    fun `parses fractional seconds`() {
        assertEquals(expected, parsePublishedAt("2026-10-13T19:06:00.1234567Z"))
    }

    @Test
    fun `parses offset timestamp`() {
        assertEquals(expected, parsePublishedAt("2026-10-13T21:06:00+02:00"))
    }

    @Test
    fun `missing or malformed values are null`() {
        assertNull(parsePublishedAt(null))
        assertNull(parsePublishedAt(""))
        assertNull(parsePublishedAt("yesterday"))
        assertNull(parsePublishedAt("2026-10-13"))
    }
}
