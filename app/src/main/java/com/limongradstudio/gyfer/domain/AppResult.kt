package com.limongradstudio.gyfer.domain

sealed class AppResult<out T> {
    data object Loading : AppResult<Nothing>()
    data class Success<out T>(val data: T? = null) : AppResult<T>()
    data class Failure(val error: Throwable? = null) : AppResult<Nothing>()
}