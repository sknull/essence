package de.visualdigits.essence.model

import com.fleeksoft.ksoup.nodes.Element

data class HtmlPart(
    val elementType: ElementType,
    val html: String? = null,
    val src: String? = null,
)
