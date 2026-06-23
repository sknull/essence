package de.visualdigits.essence

import com.fleeksoft.ksoup.nodes.Element

data class EssenceResult(
    val text: String,
    val html: Element?,
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
