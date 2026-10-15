package com.ohieku.newsapp.extensions.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private val FRACTIONAL_SECONDS = Regex("""\.\d+""")

/**
 * Parses NewsAPI's ISO-8601 `publishedAt` (e.g. `2026-10-13T19:06:00Z`) into epoch millis,
 * or null when it is missing or malformed.
 */
fun parsePublishedAt(value: String?): Long? {
    if (value.isNullOrBlank()) return null

    // SimpleDateFormat has no pattern for variable-length fractions; they only matter below a second
    val withoutFraction = value.trim().replaceFirst(FRACTIONAL_SECONDS, "")
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).apply { isLenient = false }

    return try {
        format.parse(withoutFraction)?.time
    } catch (e: ParseException) {
        null
    }
}

/** The calendar day a story belongs to, for the feed's day headers and the time column. */
sealed interface StoryDay {
    data object Today : StoryDay
    data object Yesterday : StoryDay
    /** [startOfDay] is the local midnight that opens that day, so stories on the same day compare equal. */
    data class OnDate(val startOfDay: Long) : StoryDay
}

fun storyDay(publishedMillis: Long, nowMillis: Long, timeZone: TimeZone = TimeZone.getDefault()): StoryDay {
    val startOfToday = startOfDay(nowMillis, timeZone)
    // Calendar arithmetic rather than a fixed 24h, so a daylight-saving change can't shift "yesterday"
    val startOfYesterday = Calendar.getInstance(timeZone).apply {
        timeInMillis = startOfToday
        add(Calendar.DAY_OF_YEAR, -1)
    }.timeInMillis
    return when {
        // Clock skew can put a story slightly in the future; it still belongs to today
        publishedMillis >= startOfToday -> StoryDay.Today
        publishedMillis >= startOfYesterday -> StoryDay.Yesterday
        else -> StoryDay.OnDate(startOfDay(publishedMillis, timeZone))
    }
}

private fun startOfDay(millis: Long, timeZone: TimeZone): Long = Calendar.getInstance(timeZone).apply {
    timeInMillis = millis
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis
