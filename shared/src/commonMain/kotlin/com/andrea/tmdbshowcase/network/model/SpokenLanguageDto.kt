package com.andrea.tmdbshowcase.network.model

import com.andrea.tmdbshowcase.core.model.SpokenLanguage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpokenLanguageDto(

    @SerialName("english_name")
    val englishName: String? = null,

    @SerialName("iso_639_1")
    val iso6391: String? = null,

    @SerialName("name")
    val name: String? = null
)

fun SpokenLanguageDto.toSpokenLanguage(): SpokenLanguage {
    return SpokenLanguage(englishName ?: "")
}
