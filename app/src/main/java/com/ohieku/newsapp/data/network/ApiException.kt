package com.ohieku.newsapp.data.network

class ApiException: Exception {
    constructor(message: String): this(0, message)

    constructor(statusCode: Int, message: String): super(message) {
        this.statusCode = statusCode
    }

    private val statusCode: Int
}