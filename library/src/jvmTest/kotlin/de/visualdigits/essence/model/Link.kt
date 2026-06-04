package de.visualdigits.essence.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Link(
    @SerialName("text") val text: String,
    @SerialName("href") val href: String
)
