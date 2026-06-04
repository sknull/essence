package de.visualdigits.essence.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Expectations(
    @SerialName("url") val url: String,
    @SerialName("target_language") val targetLanguage: String? = null,
    @SerialName("expected") val expected: Expected
)
