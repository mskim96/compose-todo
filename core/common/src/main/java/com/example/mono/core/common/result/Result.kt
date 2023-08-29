package com.example.mono.core.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface Result<out T> {
    data object Loading : Result<Nothing>
    data class Error(val exception: Throwable? = null) : Result<Nothing>
    data class Success<T>(val data: T) : Result<T>
}

/**
 * An extension function that wraps a Flow element into a Result.
 *
 * @return wrap [Flow] element into a [Result].
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> =
    this.map<T, Result<T>> {
        Result.Success(it)
    }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it)) }