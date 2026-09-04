package de.visualdigits.essence.model

import com.fleeksoft.ksoup.nodes.Element

class HtmlPart(
    val elementType: ElementType,
    html: List<Element> = listOf()
) : Part(
    html
) {
    override fun toString(): String {
        return "HtmlPart(elementType=$elementType, html=$html)"
    }
}

