package com.ohieku.newsapp.extensions.utils

import com.ohieku.newsapp.data.network.ApiException
import retrofit2.Response

fun <T> getException(response: Response<T>): ApiException {
    response.errorBody()?.close()

    val message = when (response.code()) {
        400 -> "Bad request"
        401 -> "News is unavailable right now, try again later"
        404 -> "Resource not found"
        429 -> "Too many requests, try again later"
        else -> "Cannot contact server, try again later"
    }

    return ApiException(response.code(), message)
}
