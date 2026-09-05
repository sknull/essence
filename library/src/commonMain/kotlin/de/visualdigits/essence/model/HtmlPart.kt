package de.visualdigits.essence.model

import com.fleeksoft.ksoup.nodes.Element

data class HtmlPart(
    override val html: List<Element> = listOf(),
    val elementType: ElementType
) : Part
