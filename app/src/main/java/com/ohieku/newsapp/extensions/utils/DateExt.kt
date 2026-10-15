package com.ohieku.newsapp.extensions.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

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

/** How long ago a story was published, bucketed for the running order's compact time column. */
sealed interface TimeAgo {
    data object JustNow : TimeAgo
    data class Minutes(val count: Long) : TimeAgo
    data class Hours(val count: Long) : TimeAgo
    data class Days(val count: Long) : TimeAgo
    data class OnDate(val millis: Long) : TimeAgo
}

fun timeAgo(publishedAtMillis: Long, nowMillis: Long): TimeAgo {
    // Clock skew can put a story slightly in the future; treat it as just published
    val elapsed = (nowMillis - publishedAtMillis).coerceAtLeast(0)
    return when {
        elapsed < TimeUnit.MINUTES.toMillis(1) -> TimeAgo.JustNow
        elapsed < TimeUnit.HOURS.toMillis(1) -> TimeAgo.Minutes(TimeUnit.MILLISECONDS.toMinutes(elapsed))
        // Hours run to 48 so the free plan's day-old headlines still read in order ("26h", "31h"), not all "1d"
        elapsed < TimeUnit.HOURS.toMillis(48) -> TimeAgo.Hours(TimeUnit.MILLISECONDS.toHours(elapsed))
        elapsed < TimeUnit.DAYS.toMillis(7) -> TimeAgo.Days(TimeUnit.MILLISECONDS.toDays(elapsed))
        else -> TimeAgo.OnDate(publishedAtMillis)
    }
}
