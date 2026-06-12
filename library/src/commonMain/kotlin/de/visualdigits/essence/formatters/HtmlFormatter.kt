package de.visualdigits.essence.formatters

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.LeafNode
import com.fleeksoft.ksoup.nodes.Node
import de.visualdigits.essence.util.find
import de.visualdigits.essence.words.StopWords

class HtmlFormatter(private val stopWords: StopWords) : Formatter {

    companion object {
        private val tagsToDelete = listOf(
            "aside"
        )
        private val tagsToRetain = listOf(
            "#text",
            "a",
            "abbr",
            "b",
            "br",
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
            "span",
            "strong",
            "svg",
            "u",
            "ul",
            "ol"
        )

        private val attributesToRetain = listOf(
            "href",
            "src",
            "target"
        )
    }

    override fun format(element: Element?): String {
        return formatElement(element)?.html()?:""
    }

    fun formatElement(element: Element?): Element? {
        return element?.let { n ->
            removeNegativescoresNodes(n)
            cleanupTags(n)
            removeEmptyTags(n)
            cleanupDivs(n)
            cleanupAttributes(n)
            n
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

    private fun removeEmptyTags(node: Node) {
        node.childNodes().forEach { child -> removeEmptyTags(child) }
        if ((node !is LeafNode && node.childNodes().isEmpty()) || (node is LeafNode && node.coreValue().trim().isEmpty())) {
            node.remove()
        }
    }

    private fun removeNegativescoresNodes(element: Element) {
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
        val tagName = node.nodeName().lowercase()
        val retainElement = tagsToRetain.contains(tagName)
        if (!retainElement) {
            node.remove()
        }
    }

    fun cleanupDivs(element: Node) {
        element.childNodes().forEach { c -> cleanupDivs(c) }
        if (element.nodeName().lowercase() == "div" && element.parent()?.nodeName()?.lowercase() == "div") {
            element.unwrap()
        }
    }
}
