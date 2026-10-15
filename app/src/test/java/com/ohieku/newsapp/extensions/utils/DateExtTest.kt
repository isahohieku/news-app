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

    private val now = expected

    @Test
    fun `buckets elapsed time for the time column`() {
        val minute = 60_000L
        assertEquals(TimeAgo.JustNow, timeAgo(now - 30_000, now))
        assertEquals(TimeAgo.Minutes(41), timeAgo(now - 41 * minute, now))
        assertEquals(TimeAgo.Hours(23), timeAgo(now - (23 * 60 + 59) * minute, now))
        assertEquals(TimeAgo.Hours(47), timeAgo(now - (47 * 60 + 59) * minute, now))
        assertEquals(TimeAgo.Days(2), timeAgo(now - 48 * 60 * minute, now))
        assertEquals(TimeAgo.Days(6), timeAgo(now - 6 * 24 * 60 * minute, now))
        assertEquals(TimeAgo.OnDate(now - 8 * 24 * 60 * minute), timeAgo(now - 8 * 24 * 60 * minute, now))
    }

    @Test
    fun `a story from the near future counts as just published`() {
        assertEquals(TimeAgo.JustNow, timeAgo(now + 90_000, now))
    }
}
