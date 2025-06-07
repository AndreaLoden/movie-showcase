package com.andrea.imdbshowcase.network.utils

enum class TheMovieDataBaseError {
    ServiceUnavailable,
    ClientError,
    ServerError,
    UnknownError
}

class TheMovieDataBaseException(error: TheMovieDataBaseError) : Exception(
    "Something goes wrong: $error"
)

class MissingAPIKeyException : Exception("Please add your API Key.")
