package com.ohieku.newsapp.extensions.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.TimeZone

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

    private val utc = TimeZone.getTimeZone("UTC")

    // 2026-10-14T09:00:00Z
    private val now = 1791968400000L
    private val hour = 3_600_000L

    @Test
    fun `stories are grouped by calendar day`() {
        assertEquals(StoryDay.Today, storyDay(now - 8 * hour, now, utc))
        assertEquals(StoryDay.Yesterday, storyDay(now - 10 * hour, now, utc))
        assertEquals(StoryDay.Yesterday, storyDay(now - 33 * hour, now, utc))
        // 2026-10-12T15:00:00Z belongs to Oct 12, whose midnight is 2026-10-12T00:00:00Z
        assertEquals(StoryDay.OnDate(1791763200000L), storyDay(now - 42 * hour, now, utc))
    }

    @Test
    fun `stories on the same earlier day share one day`() {
        assertEquals(storyDay(now - 40 * hour, now, utc), storyDay(now - 50 * hour, now, utc))
    }

    @Test
    fun `a story from the near future belongs to today`() {
        assertEquals(StoryDay.Today, storyDay(now + 90_000, now, utc))
    }
}
