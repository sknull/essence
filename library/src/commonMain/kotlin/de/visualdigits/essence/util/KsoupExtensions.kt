package de.visualdigits.essence.util

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.LeafNode
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import de.visualdigits.essence.formatters.Partition


private val emptyTags: List<String> = listOf(
    "br",
    "img"
)

private val attributesToRetain: List<String> = listOf(
    "href",
    "src",
    "target",
    "alt",
    "title",
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

fun Node.removeEmptyTags() {
    childNodes().forEach { child -> child.removeEmptyTags() }
    if (
        !emptyTags.contains(nodeName().lowercase())
        && (
                (this !is LeafNode && childNodes().isEmpty())
                        || (this is LeafNode && coreValue().trim().isEmpty())
                        || (this is TextNode && getWholeText().trim().isBlank())
                )
    ) {
        remove()
    }
}

fun Node.removeUnwantedTags(tagsToRetain: List<String>) {
    childNodes().forEach { c -> c.removeUnwantedTags(tagsToRetain) }
    val nodeName = nodeName().lowercase()
    if (!tagsToRetain.contains(nodeName) && nodeName != "#text") {
        remove()
    }
}

fun Element.cleanupElement(): Element {
    cleanupAttributes()
    unwrapDivs()

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
    if (nodeName() == "div" && hasParent("div")) {
        unwrap()
    }

    return this
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
