package de.visualdigits.essence.model

import com.fleeksoft.ksoup.nodes.Element

data class ImagePart(
    override val html: List<Element> = listOf(),
    val images: List<ImageEntry>,
    val previousSibling: Element? = null
) : Part
