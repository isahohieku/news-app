package com.ohieku.newsapp.domain.common

enum class ErrorType {
    NoInternet,
    Timeout,
    CannotConnect,
    BadRequest,
    Unavailable,
    NotFound,
    RateLimited,
    Server,
    Unknown,
}

/** A failure the app knows how to explain; the data layer subclasses it with transport details. */
open class AppException(
    val type: ErrorType,
    message: String = type.name,
    cause: Throwable? = null,
) : Exception(message, cause)
