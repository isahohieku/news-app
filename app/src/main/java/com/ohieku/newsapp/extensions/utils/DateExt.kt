package com.ohieku.newsapp.extensions.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

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
