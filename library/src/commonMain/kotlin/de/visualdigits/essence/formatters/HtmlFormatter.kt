package de.visualdigits.essence.formatters

import com.fleeksoft.ksoup.nodes.Element
import de.visualdigits.essence.model.ElementType
import de.visualdigits.essence.model.HtmlPart
import de.visualdigits.essence.model.ImageEntry
import de.visualdigits.essence.model.ImagePart
import de.visualdigits.essence.model.ImageType
import de.visualdigits.essence.model.Part
import de.visualdigits.essence.util.cleanupAttributes
import de.visualdigits.essence.util.cleanupElement
import de.visualdigits.essence.util.isTag
import de.visualdigits.essence.util.partitionBy
import de.visualdigits.essence.util.removeEmptyTags
import de.visualdigits.essence.util.removeUnwantedTags
import de.visualdigits.essence.util.unwrapDivs

class HtmlFormatter : Formatter() {

    companion object {

        val tagsToRetain: List<String> = listOf(
            "a",
            "abbr",
            "b",
            "br",
            "cite",
            "div",
            "embed",
            "h1",
            "h2",
            "h3",
            "h4",
            "i",
            "img",
            "li",
            "object",
            "p",
            "picture",
            "span",
            "strong",
            "svg",
            "u",
            "ul",
            "ol"
        )

        private val standardImageTypes = listOf(".jpg", ".png", ".webp")
        private val iconImageTypes = listOf(".svg", ".ico")

    }

    override fun format(element: Element?): String {
        return ""
    }

    fun formatElement(element: Element?): List<Part> {
        val html = element ?: Element("main")
        html.tagName("main")
        html.removeNegativeScoredNodes()
        html.removeUnwantedTags(tagsToRetain)
        html.removeEmptyTags()
        html.unwrapDivs()
        html.select("picture").forEach { it.unwrap() }
        html.children().forEach { it.select("span").forEach { s -> s.prepend(" ").unwrap() } }

        val partitionBy = html.children()
            .partitionBy { it?.isTag("img") == true || it?.select("img")?.isNotEmpty() == true }
        val htmlParts = partitionBy
            .flatMap { imagePartition ->
                if (imagePartition.element != null) {
                    val (container, images) = if (imagePartition.element.nodeName().lowercase() == "img") {
                        null to listOf(imagePartition.element)
                    } else {
                        imagePartition.element to imagePartition.element.select("img").toList()
                    }
                    val imageEntries = images.map { image ->
                        val src = image.attr("src").lowercase()
                        val imageType = if (standardImageTypes.any { src.contains(it) }) {
                            ImageType.standard
                        } else if (iconImageTypes.any { src.contains(it) }) {
                            ImageType.icon
                        } else {
                            ImageType.unknown
                        }

                        ImageEntry(
                            src = image.attr("src"),
                            alt = image.attr("alt"),
                            title = image.attr("title"),
                            imageType = imageType
                        )
                    }
                    images.forEach { it.remove() }
                    val elements = if (container != null && container != html && container.childNodes().isNotEmpty()) {
                        container.select("img").forEach { it.remove() }
                        container.remove()
                        container.cleanupElement()
                        if (container.isTag("div")) {
                            container.children()
                        } else {
                            listOf(container)
                        }
                    } else {
                        listOf()
                    }.filter { elem -> !elem.isTag("a") || elem.childrenSize() > 0 }
                    val listOf = listOf(
                        ImagePart(
                            html = elements,
                            images = imageEntries,
                            previousSibling = (container ?: images.first()).previousElementSibling()
                        )
                    )
                    listOf
                } else {
                    imagePartition.elements
                        .partitionBy { it?.isTag("div") == true }
                        .flatMap { divPartition ->
                            if (divPartition.element != null) {
                                val divHtml = divPartition.element.children().map { it.cleanupElement() }
                                listOf(
                                    HtmlPart(
                                        elementType = if (divHtml.size == 1) ElementType.paragraph else ElementType.div,
                                        html = divHtml
                                    )
                                )
                            } else if (divPartition.elements.isNotEmpty()) {
                                divPartition.elements
                                    .partitionBy { it?.nodeName()?.lowercase()?.startsWith("h") == true }
                                    .mapNotNull { headerPartition ->
                                        if (headerPartition.element != null) {
                                            val headerHtml = headerPartition.element.cleanupElement()
                                            HtmlPart(
                                                elementType = ElementType.headline,
                                                html = listOf(headerHtml)
                                            )
                                        } else if (headerPartition.elements.isNotEmpty()) {
                                            val paragraphHtml = headerPartition.elements.map { it.cleanupElement() }
                                            if (paragraphHtml.isNotEmpty()) {
                                                HtmlPart(
                                                    elementType = ElementType.paragraph,
                                                    html = paragraphHtml
                                                )
                                            } else null
                                        } else {
                                            null
                                        }
                                    }
                            } else {
                                listOf()
                            }
                        }
                }
            }

        return htmlParts
    }
}
