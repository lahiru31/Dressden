package com.dressden.app.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()

    fun <R> map(transform: (T) -> R): Resource<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(message)
        is Loading -> Loading
    }

    companion object {
        fun <T> loading(): Resource<T> = Loading
        fun <T> success(data: T): Resource<T> = Success(data)
        fun <T> error(message: String): Resource<T> = Error(message)
    }
}

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> Response<RequestType>,
    crossinline saveFetchResult: suspend (Response<RequestType>) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline onFetchError: (String) -> Unit = { }
) = flow {
    emit(Resource.Loading)

    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading)

        try {
            val response = fetch()
            if (response.isSuccessful) {
                saveFetchResult(response)
                query().map { Resource.Success(it) }
            } else {
                onFetchError(response.message())
                query().map { Resource.Error(response.message()) }
            }
        } catch (throwable: Throwable) {
            onFetchError(throwable.message ?: "Unknown error occurred")
            query().map { Resource.Error(throwable.message ?: "Unknown error occurred") }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}

fun <T> Response<T>.toResource(): Resource<T> {
    return try {
        if (isSuccessful) {
            body()?.let {
                Resource.Success(it)
            } ?: Resource.Error("Response body is null")
        } else {
            Resource.Error(message())
        }
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Unknown error occurred")
    }
}

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): Resource<T> {
    return try {
        val response = apiCall()
        response.toResource()
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Unknown error occurred")
    }
}

fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) {
        action(data)
    }
    return this
}

fun <T> Resource<T>.onError(action: (String) -> Unit): Resource<T> {
    if (this is Resource.Error) {
        action(message)
    }
    return this
}

fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> {
    if (this is Resource.Loading) {
        action()
    }
    return this
}

fun <T> Resource<T>.getOrNull(): T? = when (this) {
    is Resource.Success -> data
    else -> null
}

fun <T> Resource<T>.getOrDefault(default: T): T = when (this) {
    is Resource.Success -> data
    else -> default
}

fun <T> Resource<T>.isSuccess(): Boolean = this is Resource.Success
fun <T> Resource<T>.isError(): Boolean = this is Resource.Error
fun <T> Resource<T>.isLoading(): Boolean = this is Resource.Loading

fun <T> Resource<T>.requireData(): T = when (this) {
    is Resource.Success -> data
    is Resource.Error -> throw IllegalStateException("Resource is in error state: $message")
    is Resource.Loading -> throw IllegalStateException("Resource is in loading state")
}

fun <T, R> Resource<T>.fold(
    onSuccess: (T) -> R,
    onError: (String) -> R,
    onLoading: () -> R
): R = when (this) {
    is Resource.Success -> onSuccess(data)
    is Resource.Error -> onError(message)
    is Resource.Loading -> onLoading()
}

suspend fun <T, R> Resource<T>.suspendFold(
    onSuccess: suspend (T) -> R,
    onError: suspend (String) -> R,
    onLoading: suspend () -> R
): R = when (this) {
    is Resource.Success -> onSuccess(data)
    is Resource.Error -> onError(message)
    is Resource.Loading -> onLoading()
}
