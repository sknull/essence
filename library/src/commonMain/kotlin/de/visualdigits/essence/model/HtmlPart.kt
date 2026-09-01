package de.visualdigits.essence.model

import com.fleeksoft.ksoup.nodes.Element

data class HtmlPart(
    val elementType: ElementType,
    val html: List<Element> = listOf(),
    val imageType: ImageType? = null,
    val src: String? = null,
)
