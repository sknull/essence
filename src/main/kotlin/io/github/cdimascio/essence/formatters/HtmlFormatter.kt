package io.github.cdimascio.essence.formatters

import io.github.cdimascio.essence.util.NodeHeuristics
import io.github.cdimascio.essence.util.find
import io.github.cdimascio.essence.words.StopWords
import org.jsoup.nodes.Element

class HtmlFormatter(private val stopWords: StopWords) : Formatter {

    companion object {
        private val tagsToRetainInParagraph = listOf("a", "b", "u", "i", "strong")
    }

    override fun format(node: Element?): String {
        return formatElement(node)?.html()?:""
    }

    fun formatElement(node: Element?): Element? {
        return node?.let {
            val bestRoot = drillDownToCruxElement(node)
            // TODO: Combine the following into a single pass
            removeNegativescoresNodes(bestRoot)
            removeFewWordsParagraphs(bestRoot)
            bestRoot.allElements.forEach { elem ->
                elem.classNames().forEach { cn ->
                    elem.removeClass(cn)
                }
                elem.attributes()
                    .filter { attr -> attr.key != "href" }
                    .forEach { attr ->
                        elem.removeAttr(attr.key)
                    }
            }
            bestRoot
        }
    }

    private fun drillDownToCruxElement(node: Element): Element {
        if (node.ownText().isBlank() && node.childNodeSize() == 1) {
            val onlyChild = node.childNode(0)
            if (onlyChild is Element) {
                drillDownToCruxElement(onlyChild)
            }
        }
        return node
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
            val numStopWords = stopWords.statistics(text).stopWords.size
            val hasObject = element.find("object").isNotEmpty()
            val hasEmbed = element.find("embed").isNotEmpty()
            val isEndline = tag == "br" || text == "\\r"
            val isHeadline = NodeHeuristics.isHeadline(element)
            val isLinkInParagraph = tagsToRetainInParagraph.contains(element.tagName()) && element.parent()?.tagName() == "p"
            if (!isLinkInParagraph && !isHeadline && !isEndline && numStopWords < 3 && !hasObject && !hasEmbed && element.parent() != null) {
                element.remove()
            } else {
                val trimmed = text.trim()
                val numWords = text.split(" ").size
                // remove paragraph's that are surrounded entirely by parens and have few-ish words
                if (trimmed.isNotBlank() && numWords < 25 && trimmed.first() == '(' && trimmed.last() == ')' && element.parent() != null) element.remove()
            }
        }
    }
}
