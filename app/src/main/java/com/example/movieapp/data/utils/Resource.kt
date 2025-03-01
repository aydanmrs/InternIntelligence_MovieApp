package com.example.movieapp.data.utils

sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T, message: String? = null) : Resource<T>(data, message)
    class Error<T>(val exception: Throwable, message: String? = null) : Resource<T>(message = message)
    object Loading : Resource<Nothing>()
}