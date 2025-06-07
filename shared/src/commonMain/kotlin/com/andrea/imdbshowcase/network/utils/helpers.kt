package com.andrea.imdbshowcase.network.utils

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

suspend inline fun <reified T> handleErrors(
    crossinline response: suspend () -> HttpResponse
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

    return@withContext try {
        result.body()
    } catch (e: Exception) {
        throw TheMovieDataBaseException(TheMovieDataBaseError.ServerError)
    }

}