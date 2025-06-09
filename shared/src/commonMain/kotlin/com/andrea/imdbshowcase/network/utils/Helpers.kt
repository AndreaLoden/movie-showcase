package com.andrea.imdbshowcase.network.utils

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

object Helpers {

    suspend inline fun <reified T> handleErrors(
        noinline response: suspend () -> HttpResponse
    ): T = handleErrorsImpl(response, typeInfo<T>())

    suspend fun <T> handleErrorsImpl(
        response: suspend () -> HttpResponse,
        typeInfo: TypeInfo
    ): T = withContext(Dispatchers.IO) {
        val result = try {
            response()
        } catch (e: IOException) {
            throw TheMovieDataBaseException(TheMovieDataBaseError.ServiceUnavailable)
        }

        when (result.status.value) {
            in 200..299 -> Unit
            in 400..499 -> throw TheMovieDataBaseException(TheMovieDataBaseError.ClientError)
            500 -> throw TheMovieDataBaseException(TheMovieDataBaseError.ServerError)
            else -> throw TheMovieDataBaseException(TheMovieDataBaseError.UnknownError)
        }

        try {
            result.body<T>(typeInfo)
        } catch (e: Exception) {
            throw TheMovieDataBaseException(TheMovieDataBaseError.ServerError)
        }
    }
}
