package io.github.cdimascio.essence.cleaners

import io.github.cdimascio.essence.util.NodeHeuristics
import io.github.cdimascio.essence.util.TraversalHelpers
import io.github.cdimascio.essence.util.find
import io.github.cdimascio.essence.words.StopWords
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

object ScoreCleaner {

    private val containerNodes = listOf("p", "a")

    fun clean(element: Element?, stopWords: StopWords) {
        val topNode = skipNonTextualTopNodes(element)
        val addSiblingsToTopNode = addSiblingsToTopNode(topNode, stopWords)
        addSiblingsToTopNode?.let { updatedElement ->
            for (child in updatedElement.children()) {
                if (!containerNodes.contains(child.tagName())) {
                    val hasHighLinkDensity =
                        NodeHeuristics.hasHighLinkDensity(child)
                    val tableOrListWithNoParagraphs =
                        NodeHeuristics.isTableOrListWithNoParagraphs(child)
                    val nodeThresholdMet =
                        NodeHeuristics.isNodeThresholdMet(updatedElement, child)
                    if ((hasHighLinkDensity ||
                        tableOrListWithNoParagraphs ||
                        !nodeThresholdMet) && child.hasParent()
                    ) {
//println("### Removing child: ${child.tagName()} - ${child.parent().tagName()} [hasHighLinkDensity=$hasHighLinkDensity, tableOrListWithNoParagraphs=$tableOrListWithNoParagraphs, nodeThresholdMet=$nodeThresholdMet]] ${child.text()}")
                            child.remove()
                    }
//                    else {
//                        println("### KEEPING child: ${child.tagName()} - ${child.parent().tagName()} [hasHighLinkDensity=$hasHighLinkDensity, tableOrListWithNoParagraphs=$tableOrListWithNoParagraphs, nodeThresholdMet=$nodeThresholdMet]] ${child.text()}")
//                    }
                }
            }
        }
    }

    private fun skipNonTextualTopNodes(targetNode: Element?): Element? {
        if (targetNode?.ownText()?.isBlank() == true && targetNode.childNodeSize() == 1) {
            val child = targetNode.childNodes()[0]
            if (child is Element) {
                return skipNonTextualTopNodes(child)
            }
        }
        return targetNode
    }

    // Why add only previous siblings -- change name of function
    private fun addSiblingsToTopNode(targetNode: Element?, stopWords: StopWords): Element? {
        val baselineParagraphSiblingScore = getSiblingsScore(targetNode, stopWords)
        if (targetNode == null) return null

        val previousSiblings = TraversalHelpers.getAllPreviousSiblings(targetNode)
        previousSiblings.filterIsInstance<Element>().forEach { sib: Node ->
            val siblingContent = getSiblingsContent(sib as Element, baselineParagraphSiblingScore, stopWords)
            for (content in siblingContent) {
                if (content.isNotBlank()) {
                    targetNode.prependChild(TextNode(content))
                }
            }
        }
        return targetNode
    }

    private fun getSiblingsContent(node: Element, score: Double, stopWords: StopWords): List<String> {
        if (node.tagName() == "p" && node.text().isNotBlank()) {
            return listOf(node.text())
        }
        val candidateParagraphs = node.find("p")
        if (candidateParagraphs.isEmpty()) {
            return emptyList()
        }
        val contents = mutableListOf<String>()
        candidateParagraphs.forEach { p ->
            val text = p.text()
            if (text.isNotBlank()) {
                val stats = stopWords.statistics(text)
                val paragraphScore = stats.stopWords.size
                val siblingBaselineScore = 0.30
                val hasHighLinkDensity = NodeHeuristics.hasHighLinkDensity(p)
                val sibScore = score * siblingBaselineScore

                if (sibScore < paragraphScore && !hasHighLinkDensity) {
                    contents.add(text)
                }
            }
        }
        return contents
    }

    private fun getSiblingsScore(topNode: Element?, stopWords: StopWords): Double {
        val base = 100000.0
        var paragraphsNum = 0
        var paragraphsScore = 0.0
        if (topNode == null) return base

        val elementsToCheck = topNode.find("p")
        elementsToCheck.forEach { element ->
            val text = element.text()
            val stats = stopWords.statistics(text)
            val hasHighLinkDensity = NodeHeuristics.hasHighLinkDensity(element)
            if (stats.stopWords.size > 2 && !hasHighLinkDensity) {
                paragraphsNum += 1
                paragraphsScore += stats.stopWords.size
            }
        }
        if (paragraphsNum > 0) {
            return paragraphsScore / paragraphsNum
        }
        return base
    }
}
