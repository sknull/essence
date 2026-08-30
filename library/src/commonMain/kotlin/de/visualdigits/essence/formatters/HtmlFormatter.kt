package de.visualdigits.essence.formatters

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.LeafNode
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import de.visualdigits.essence.util.find

class HtmlFormatter : Formatter {

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
    }

    override fun format(element: Element?): String {
        return formatElement(element).first.html()
    }

    fun formatElement(element: Element?): Pair<Element, List<Element>> {
        val html = element?.let { elem ->
            removeNegativeScoresNodes(elem)
            cleanupTags(elem)
            removeEmptyTags(elem)
            cleanupDivs(elem)
            elem
        } ?: Element("div")

        val clone = html.clone()
        clone.select("picture").forEach { it.unwrap() }

        cleanupAttributes(html)
        cleanupAttributes(clone)
        clone.children().forEach { unwrapDivs(it) }
        clone.tagName("div")

        val imageElements = clone.select("img")
        val images = imageElements
            .filter { image -> image.parent() == clone }
            .map { image -> Pair(image, image.previousSibling()) }

        val childNodes = clone.childNodes()
        val indices = images.map { childNodes.indexOf(it.first) }.toMutableList()
        val parts = if (images.isNotEmpty()) {
            if (indices.first() != 0) indices.add(0, 0)
            val chunks = indices.dropLast(1).mapIndexed { index, i -> Pair(i, indices[index + 1]) }.toMutableList()
            if (chunks.last().second < childNodes.size - 1) chunks.add(Pair(chunks.last().second, childNodes.size))
            val parts = chunks.map { chunk ->
                val elem = Element(tag = "div")
                elem.addChildren(*childNodes.subList(chunk.first, chunk.second).toTypedArray())
                elem
            }.toMutableList()
            images.forEach { image ->
                if (image.second != null) {
                    parts.find { part ->
                        part.childNodes().contains(image.second) }?.also { part ->
                        parts.add(parts.indexOf(part) + 1, image.first)
                    }
                } else {
                    parts.add(0, image.first)
                }
            }

            parts
        } else {
            listOf(html)
        }
        clone
            .select(TAGS_TO_DELETE_SELECTOR)
            .forEach { elem -> elem.remove() }
        html
            .select(TAGS_TO_DELETE_SELECTOR)
            .forEach { elem -> elem.remove() }

        return Pair(html, parts)
    }

    private fun removeNegativeScoresNodes(element: Element) {
        val gravityElements = element.find("*[gravityScore]")
        gravityElements.forEach {
            val score = try {
                it.attr("gravityScore").toDouble()
            } catch (_: NumberFormatException) {
                0.0
            }

            if (score < 0.0) {
                it.remove()
            }
        }
    }

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

    fun cleanupDivs(element: Node) {
        element.childNodes().forEach { c -> cleanupDivs(c) }
        if (element.nodeName().lowercase() == "div" && element.parent()?.nodeName()?.lowercase() == "div") {
            element.unwrap()
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

    private fun unwrapDivs(element: Element?) {
        element?.children()?.forEach { c -> unwrapDivs(c) }
        if (element?.nodeName() == "div") {
            element.unwrap()
        }
    }
}
