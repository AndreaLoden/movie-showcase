package com.andrea.tmdbshowcase.network.utils

enum class TheMovieDataBaseError {
    ServiceUnavailable,
    ClientError,
    ServerError,
    UnknownError
}

class TheMovieDataBaseException(val error: TheMovieDataBaseError) : Exception(
    "Something goes wrong: $error"
)

class MissingAPIKeyException : Exception("Please add your API Key.")
