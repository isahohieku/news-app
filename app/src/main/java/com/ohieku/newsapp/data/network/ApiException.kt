package com.ohieku.newsapp.data.network

class ApiException(
    val statusCode: Int,
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    constructor(message: String, cause: Throwable? = null) : this(0, message, cause)
}
