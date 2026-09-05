package de.visualdigits.essence.util

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import de.visualdigits.essence.formatters.Partition


private val emptyTags: List<String> = listOf(
    "br",
    "img",
    "#text"
)

private val attributesToRetain: List<String> = listOf(
    "href",
    "src",
    "target",
    "alt",
    "title",
)

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
    "figure",
    "figcaption",
    "span",
    "strong",
    "svg",
    "u",
    "ul",
    "ol"
)

val forceRemove = listOf(
    "noscript",
    "template"
)

fun Node.isTag(nodeName: String): Boolean = nodeName().equals(nodeName, ignoreCase = true)

fun Element.hasParent(nodeName: String): Boolean {
    var current = this.parent()
    while (current != null) {
        if (current.nodeName().lowercase() == nodeName) return true
        current = current.parent()
    }
    return false
}

fun Node.removeUnwantedTags() {
    childNodes().forEach { c -> c.removeUnwantedTags() }
    val nodeName = nodeName().lowercase()
    val hasImg = if (this is Element) select("img").isNotEmpty() && !forceRemove.contains(nodeName) else false
    if (!tagsToRetain.contains(nodeName) && !hasImg && nodeName != "#text") {
        remove()
    }
}

fun Element.cleanupElement(): Element {
    cleanupAttributes()
    unwrapDivs()
    removeEmptyTags()
    select("figcaption").forEach { it.unwrap() }
    select("figure").forEach { it.tagName("span") }

    return this
}

fun Element.cleanupAttributes() {
    getAllElements().forEach {
        val attributesToRemove = it.attributes()
            .filter { attr -> !attributesToRetain.contains(attr.key) }
            .map { attr -> attr.key }

        attributesToRemove.forEach { attr -> it.removeAttr(attr) }
    }
}

fun Element.unwrapDivs(): Element {
    children().forEach { c -> c.unwrapDivs() }
    if (isTag("div") && hasParent("div") && parentNode()?.isTag("main") == false) {
        unwrap()
    }

    return this
}

fun Node.isEmpty(): Boolean {
    return !emptyTags.contains(nodeName().lowercase()) && (this is Element && wholeText().trim().isBlank()) && childrenSize() == 0
}

fun Node.removeEmptyTags() {
    childNodes().forEach { child -> child.removeEmptyTags() }
    val nodeName = nodeName().lowercase()
    if (!emptyTags.contains(nodeName) && isEmpty()) {
        remove()
    }
}

fun List<Element>.partitionBy(predicate: (Element?) -> Boolean): List<Partition> {
    val indices = filter { predicate(it) }.map { indexOf(it) }.toMutableList()
    val chunks = (indices.map { Pair(it, it + 1) } + indices.dropLast(1)
        .mapIndexed { index, i -> Pair(i + 1, indices[index + 1]) }
        .filter { it.first < it.second })
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
        chunks.mapNotNull { chunk ->
            val subList = subList(chunk.first, chunk.second)
            val element = subList.firstOrNull()
            if (subList.size == 1 && element != null && predicate(element)) {
                Partition(element = element)
            } else if (subList.isNotEmpty()) {
                Partition(elements = subList.toList()) // according to Google AI destroy strong refs to old list
            } else {
                null
            }
        }
    } else if (isNotEmpty()) {
        listOf(Partition(elements = this))
    } else {
        listOf()
    }
}
