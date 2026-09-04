package de.visualdigits.essence.formatters

import com.fleeksoft.ksoup.nodes.Element

data class Partition(
    val element: Element? = null,
    val elements: List<Element> = listOf()
)
