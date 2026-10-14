package com.ohieku.newsapp.extensions.utils

import com.ohieku.newsapp.data.network.ApiException
import com.ohieku.newsapp.data.network.ErrorType
import retrofit2.Response

fun <T> getException(response: Response<T>): ApiException {
    response.errorBody()?.close()

    val type = when (response.code()) {
        400 -> ErrorType.BadRequest
        401 -> ErrorType.Unavailable
        404 -> ErrorType.NotFound
        429 -> ErrorType.RateLimited
        else -> ErrorType.Server
    }

    return ApiException(type, response.code())
}
