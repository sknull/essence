package de.visualdigits.essence.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Expected(
    @SerialName("meta_description") val metaDescription: String? = null,
    @SerialName("domain") val domain: String? = null,
    @SerialName("final_url") val finalUrl: String? = null,
    @SerialName("meta_keywords") val metaKeywords: String? = null,
    @SerialName("cleaned_text") val cleanedText: String? = null,
    @SerialName("tags") val tags: List<String> = listOf(),
    @SerialName("title") val title: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("meta_favicon") val metaFavicon: String? = null,
    @SerialName("meta_lang") val metaLang: String? = null,
    @SerialName("links") val links: List<Link> = listOf()
)
