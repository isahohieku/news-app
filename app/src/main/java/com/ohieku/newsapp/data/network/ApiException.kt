package com.ohieku.newsapp.data.network

import com.ohieku.newsapp.domain.common.AppException
import com.ohieku.newsapp.domain.common.ErrorType

class ApiException(
    type: ErrorType,
    val statusCode: Int = 0,
    cause: Throwable? = null,
) : AppException(type, if (statusCode != 0) "$type ($statusCode)" else type.name, cause)
