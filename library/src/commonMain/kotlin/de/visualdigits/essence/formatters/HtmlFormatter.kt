package de.visualdigits.essence.formatters

import com.fleeksoft.ksoup.nodes.Comment
import com.fleeksoft.ksoup.nodes.DataNode
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.LeafNode
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import de.visualdigits.essence.util.find
import de.visualdigits.essence.words.StopWords

class HtmlFormatter(private val stopWords: StopWords) : Formatter {

    companion object {
        private val tagsToRetain = listOf(
            "a",
            "b",
            "br",
            "h1",
            "h2",
            "h3",
            "h4",
            "header",
            "i",
            "img",
            "li",
            "p",
            "span",
            "strong",
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

    override fun format(node: Element?): String {
        return formatElement(node)?.html()?:""
    }

    fun formatElement(node: Element?): Element? {
        return node?.let { n ->
            removeNegativescoresNodes(n)
            removeFewWordsParagraphs(n)
            removeComments(n)
            removeEmptyTags(n)
            cleanupAttributes(n)
            n
        }
    }

    private fun cleanupAttributes(n: Element) {
        n.getAllElements().forEach { elem ->
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

    private fun removeComments(node: Node) {
        node.childNodes().forEach { n ->
            if (n is Comment) {
                n.remove()
            } else {
                removeComments(n)
            }
        }
        if (node is Element && node.childElementsList().isEmpty() && node.childNodes().isEmpty() && !tagsToRetain.contains(node.tagName().lowercase())) {
            node.remove()
        }
    }

    private fun removeNegativescoresNodes(node: Element) {
        val gravityElements = node.find("*[gravityScore]")
        gravityElements.forEach {
            val score = try {
                it.attr("gravityScore").toDouble()
            } catch (e: NumberFormatException) {
                0.0
            }

            if (score < 0.0) {
                it.remove()
            }
        }
    }

    private fun removeFewWordsParagraphs(node: Element) {
        val elements = node.find("*")
        elements.forEach { element ->
            val tag = element.tagName()
            val text = element.text()
            val hasObject = element.find("object").isNotEmpty()
            val hasEmbed = element.find("embed").isNotEmpty()
            val isEndline = tag == "br" || text == "\\r"
            val parents = element.parents().map { e -> e.tagName() }
            val retainElement = tagsToRetain.contains(element.tagName()) || parents.any { p -> tagsToRetain.contains(p) }
            if (!retainElement &&
                !isEndline &&
                !hasObject &&
                !hasEmbed &&
                element.parent() != null
            ) {
                element.remove()
            }
        }
    }
}
