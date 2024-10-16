package com.ohieku.newsapp.extensions.utils

import com.ohieku.newsapp.data.network.ApiException
import retrofit2.Response

fun <T> getException(response: Response<T>): Exception {
    val errorString = response.errorBody()?.string()

    return when (response.code()) {
        400 -> {
            ApiException(400, "Bad request")
        }

        404 -> {
            ApiException(404, "Resource not found")
        }

        500 -> {
            ApiException(500, "Cannot contact server, try again later")
        }

        else -> {
            ApiException(500, "Cannot contact server, try again later")
        }
    }
}