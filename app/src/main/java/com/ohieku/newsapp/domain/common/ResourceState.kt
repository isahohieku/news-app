package com.ohieku.newsapp.domain.common

sealed class ResourceState <T> {
    data class Success<T> (val data: T): ResourceState<T>()
    data class Error<T> (val error: Exception): ResourceState<T>()
}
