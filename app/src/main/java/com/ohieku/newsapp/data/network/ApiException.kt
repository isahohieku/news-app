package com.ohieku.newsapp.data.network

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

class ApiException(
    val type: ErrorType,
    val statusCode: Int = 0,
    cause: Throwable? = null,
) : Exception(if (statusCode != 0) "$type ($statusCode)" else type.name, cause)
