package de.visualdigits.essence.model

import com.fleeksoft.ksoup.nodes.Element

abstract class Part(
    val html: List<Element> = listOf(),
) {
    override fun toString(): String {
        return "Part(html=$html)"
    }
}
