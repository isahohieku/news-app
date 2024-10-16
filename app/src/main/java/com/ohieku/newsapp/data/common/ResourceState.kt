package com.ohieku.newsapp.data.common

sealed class ResourceState <T> {
    data class Success<T> (val data: T): ResourceState<T>()
    data class Error<T> (val error: Exception): ResourceState<T>()
}