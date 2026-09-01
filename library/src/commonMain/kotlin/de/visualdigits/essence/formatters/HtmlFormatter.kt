package de.visualdigits.essence.formatters

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.LeafNode
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import de.visualdigits.essence.model.ElementType
import de.visualdigits.essence.model.HtmlPart
import de.visualdigits.essence.model.ImageType

class HtmlFormatter : Formatter() {

    companion object {
        private val tagsToRetain: List<String> = listOf(
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

        private val emptyTags: List<String> = listOf(
            "br",
            "img"
        )

        private val attributesToRetain: List<String> = listOf(
            "href",
            "src",
            "target"
        )

        private const val TAGS_TO_DELETE_SELECTOR: String = "picture,img"

        private val standardImageTypes = listOf(".jpg", ".png", ".webp")
        private val iconImageTypes = listOf(".svg", ".ico")

    }

    override fun format(element: Element?): String {
        return formatElement(element).first.html()
    }

    fun formatElement(element: Element?): Pair<Element, List<HtmlPart>> {
        val html = element?.clone() ?: Element("main")
        html.tagName("main")
        html.let { elem ->
            removeNegativeScoredNodes(elem)
            cleanupTags(elem)
            removeEmptyTags(elem)
            cleanupNestedDivs(elem)
        }

        val clone = html.clone()

        clone.select("picture").forEach { it.unwrap() }
        val imageElements = clone.select("img")
        val images = imageElements.mapNotNull { img ->
            val dropLast = img.rootLineElements().dropLast(1)
            val div = dropLast.find { it.nodeName().lowercase() == "div" }
            if (div != null) {
                img.remove()
                val children = div.children()
                val clones = children.map { child -> child.clone() }
                img.addChildren(*clones.toTypedArray())
                val imageResult = if (img.attr("src").isNotBlank()) {
                    val elem = div.rootLineElements().find { it.parent() == clone } ?: img
                    ImageResult(
                        element = elem,
                        img = img,
                        previousSibling = div.previousElementSibling()
                    )
                } else null
                imageResult
            } else {
                if (img.hasAttr("alt")) {
                    img.addChildren(TextNode(img.attr("alt")))
                } else if (img.hasAttr("title")) {
                    img.addChildren(TextNode(img.attr("title")))
                }
                if (img.attr("src").isNotBlank()) {
                    val elem = img.rootLineElements().find { it.parent() == clone } ?: img
                    ImageResult(
                        element = elem,
                        img = img,
                        previousSibling = img.previousElementSibling()
                    )
                } else null
            }
        }
//        src = img.attr("src"),
//        alt = if (img.hasAttr("alt")) img.attr("alt") else img.attr(("title")),

        val children = clone.children().toList()
        val indices = images.map { image ->
            val index = children.indexOf(image.element)
            if (index != -1) index else 0
        }.toMutableList()
        clone.children().forEach { unwrapDivs(it) }
        val parts = if (images.isNotEmpty()) {
            if (indices.first() > 0) indices.add(0, 0)
            val chunks = indices.dropLast(1).mapIndexed { index, i -> Pair(i, indices[index + 1]) }.toMutableList()
            if (chunks.isNotEmpty() && chunks.last().second < children.size) chunks.add(Pair(chunks.last().second, children.size))
            val chunkParts = chunks.map { chunk ->
                val elem = Element(tag = "div")
                val subList = children.subList(chunk.first, chunk.second)
                elem.addChildren(*subList.toTypedArray())
                elem
            }.toMutableList()
            images.forEach { image ->
                if (image.previousSibling != null) {
                    chunkParts.find { part ->
                        part.childNodes().contains(image.previousSibling) }?.also { part ->
                        chunkParts.add(chunkParts.indexOf(part) + 1, image.img)
                    }
                } else {
                    chunkParts.add(0, image.img)
                }
            }

            chunkParts
        } else {
            val elem = Element("div")
            elem.addChildren(*clone.children().toTypedArray())
            elem
        }

        val nodesUsedInImages = images.flatMap { image -> image.img.children()
            .filter { it.hasAttr("essenceNodeId") }
            .map { child ->  child.attr("essenceNodeId")} }
            .toSet()

        val htmlParts = parts.flatMap { part ->
            when (part.nodeName().lowercase()) {
                "img" -> {
                    cleanupAttributes(part)
                    val src = part.attr("src")
                    val srcLower = src.lowercase()
                    val imageType = if (standardImageTypes.any { srcLower.contains(it) }) {
                        ImageType.standard
                    } else if (iconImageTypes.any { srcLower.contains(it) }) {
                        ImageType.icon
                    } else {
                        ImageType.unknown
                    }
                    listOf(HtmlPart(
                        elementType = ElementType.image,
                        html = part.children(),
                        imageType = imageType,
                        src = src
                    ))
                }
                "div" -> {
                    part.children()
                        .toList()
                        .partitionBy { it?.tagName()?.lowercase() == "div" }
                        .flatMap { partition ->
                            if (partition.element != null && partition.element.childrenSize() > 0) {
                                val filteredElements = partition.element.children()
                                    .filter { !it.hasAttr("essenceNodeId") || !nodesUsedInImages.contains(it.attr("essenceNodeId")) }
                                val elem = Element("div")
                                elem.addChildren(*filteredElements.toTypedArray())
                                removeEmptyTags(elem)
                                cleanupAttributes(elem)
                                if (elem.childrenSize() > 0) {
                                    listOf(HtmlPart(
                                        elementType = ElementType.div,
                                        html = elem.children()
                                    ))
                                } else listOf()
                            } else if (partition.elements.isNotEmpty()) {
                                val filteredElements = partition.elements.filter {
                                    !it.hasAttr("essenceNodeId") || !nodesUsedInImages.contains(it.attr("essenceNodeId"))
                                }
                                val elem = Element("div")
                                elem.addChildren(*filteredElements.toTypedArray())
                                removeEmptyTags(elem)
                                cleanupAttributes(elem)
                                elem.children()
                                    .partitionBy { it?.tagName()?.lowercase()?.startsWith("h") == true }
                                    .mapNotNull { partition ->
                                        if (partition.element != null) {
                                            HtmlPart(
                                                elementType = ElementType.headline,
                                                html = listOf(partition.element)
                                            )
                                        } else if (partition.elements.isNotEmpty()) {
                                            HtmlPart(
                                                elementType = ElementType.paragraph,
                                                html = partition.elements
                                            )
                                        } else {
                                            null
                                        }
                                    }
                            } else listOf()
                        }
                }
                else -> listOf()
            }
        }

        html.select(TAGS_TO_DELETE_SELECTOR)
            .forEach { elem -> elem.remove() }
        cleanupAttributes(html)

        return Pair(html, htmlParts)
    }

    /**
     * Keep only wanted tags.
     */
    private fun cleanupTags(node: Node) {
        node.childNodes().forEach { ce -> cleanupTags(ce) }
        val nodeName = node.nodeName().lowercase()
        if (!tagsToRetain.contains(nodeName) && nodeName != "#text") {
            node.remove()
        }
    }

    private fun removeEmptyTags(node: Node) {
        node.childNodes().forEach { child -> removeEmptyTags(child) }
        if (
            !emptyTags.contains(node.nodeName().lowercase())
            && (
                    (node !is LeafNode && node.childNodes().isEmpty())
                    || (node is LeafNode && node.coreValue().trim().isEmpty())
                    || (node is TextNode && node.getWholeText().trim().isBlank())
               )
        ) {
            node.remove()
        }
    }

    fun cleanupNestedDivs(element: Element) {
        element.children().forEach { c -> cleanupNestedDivs(c) }
        if (element.nodeName().lowercase() == "div") {
            val rootLine = element.rootLine().dropLast(1)
            if (element.childNodes().size < 2 || rootLine.contains("div")) {
                element.unwrap()
            }
        }
    }

    private fun cleanupAttributes(element: Element) {
        element.getAllElements().forEach { elem ->
            elem.attributes()
                .filter { attr -> !attributesToRetain.contains(attr.key) }
                .forEach { attr ->
                    elem.removeAttr(attr.key)
                }
        }
    }

    private fun unwrapDivs(node: Element?) {
        node?.children()?.forEach { c -> unwrapDivs(c) }
        val rootLine = node?.rootLine()?.drop(1) ?: listOf()
        if (node?.nodeName() == "div" && rootLine.contains("div")) {
            node.unwrap()
        }
    }
}

private fun Element.rootLine(rootLine: MutableList<String> = mutableListOf()): List<String> {
    return rootLineElements().map { it.nodeName().lowercase() }
}

private fun Element.rootLineElements(rootLine: MutableList<Element> = mutableListOf()): List<Element> {
    rootLine.add(0, this)
    parent()?.rootLineElements(rootLine)

    return rootLine
}

private data class ImageResult(
    val element: Element,
    val img: Element,
    val previousSibling: Element? = null,
)

fun List<Element>.partitionBy(predicate: (Element?) -> Boolean): List<Partition> {
    val indices = filter { predicate(it) }.map { indexOf(it) }.toMutableList()
    val chunks = (indices.map { Pair(it, it + 1) } + indices.dropLast(1).mapIndexed { index, i -> Pair(i + 1, indices[index + 1]) })
        .sortedBy { it.first }
        .toMutableList()
    return if (chunks.isNotEmpty()) {
        val first = chunks.first().first
        if (first > 0) {
            chunks.add(0, Pair(0, first))
        }
        val last = chunks.last().second
        if (last < size) {
            chunks.add(Pair(last, size))
        }
        chunks.map { chunk ->
            val subList = subList(chunk.first, chunk.second)
            val element = subList.firstOrNull()
            if (subList.size == 1 && predicate(element)) {
                Partition(element = element)
            } else {
                Partition(elements = subList)
            }
        }
    } else {
        listOf(Partition(elements = this))
    }
}

data class Partition(
    val element: Element? = null,
    val elements: List<Element> = listOf()
)
