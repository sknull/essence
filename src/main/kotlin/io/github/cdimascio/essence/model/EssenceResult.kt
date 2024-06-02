package io.github.cdimascio.essence.model

data class EssenceResult(
    val text: String,
    val videos: String,
    val language: String,
    val authors: List<String>,
    val title: String,
    val softTitle: String,
    val copyright: String,
    val date: String,
    val publisher: String,
    val description: String,
    val favicon: String,
    val image: String,
    val links: List<Link>,
    val canonicalLink: String,
    val keywords: String,
    val tags: List<String>
)
