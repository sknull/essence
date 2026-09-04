package de.visualdigits.essence.model

import com.fleeksoft.ksoup.nodes.Element

class ImagePart(
    html: List<Element> = listOf(),
    val images: List<ImageEntry>,
    val previousSibling: Element? = null
) : Part(
    html
) {
    override fun toString(): String {
        return "ImagePart(html=$html, images=$images, previousSibling=$previousSibling)"
    }
}

