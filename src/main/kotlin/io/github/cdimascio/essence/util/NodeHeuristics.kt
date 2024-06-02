package io.github.cdimascio.essence.util

import io.github.cdimascio.essence.scorers.DocumentScorer
import io.github.cdimascio.essence.words.StopWords
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

object NodeHeuristics {

    fun isTableOrListWithNoParagraphs(element: Element): Boolean {
        val paragraphs = element.find("p")
        val remainingParagraphs = mutableListOf<Element>()
        paragraphs.forEach { p ->
            val text = p.text()
            if (text.isNotBlank() && text.length < 25) {
                p.remove()
            } else {
                remainingParagraphs.add(p)
            }
        }
        return remainingParagraphs.isNotEmpty() &&
            listOf("td", "ul", "ol")
                .contains(remainingParagraphs[0].tagName())
    }

    fun isNodeThresholdMet(parent: Node, child: Element): Boolean {
        val parentNodeScore = DocumentScorer.getScore(parent)
        val childNodeScore = DocumentScorer.getScore(child)
        val thresholdScore = parentNodeScore * 0.05
//        println("### parentNodeScore: $parentNodeScore, childNodeScore: $childNodeScore, thresholdScore: $thresholdScore")

        val isAnExcludeTags = { tag: String -> listOf("td", "ul", "ol", "blockquote").contains(tag) }
        return !(childNodeScore < thresholdScore && !isAnExcludeTags(child.tagName()))
    }

    fun hasHighLinkDensity(node: Node): Boolean {
        if (node is Element) {
            val text = node.text()
            val words = if (text.isNotBlank()) text.split(" ") else emptyList()
            val links = node.find("a")
            if (words.isEmpty() && links.isNotEmpty()) {
                return true
            } else if (links.isNotEmpty()) {
                val percentLinkWords = links.size.toDouble() / words.size.toDouble()
                val score = percentLinkWords * links.size

                return score >= 1.0
            }
        }
        return false
    }

    fun hasFewWordsAndLowFewWordNeighbors(node: Node, stopWords: StopWords): Boolean {
        if (node is Element) {
            val ownText = node.ownText()
            if (node.childNodeSize() == 0 && (ownText.isBlank() || stopWords.statistics(ownText).stopWords.size < 5)) {
                val n = 2
                if (hasFewWordPrevSiblings(node, n, stopWords) && hasFewWordNextSiblings(node, n, stopWords)) {
                    return true
                }
            }
        }
        return false
    }

    private fun hasFewWordPrevSiblings(node: Node, numSibsToCheck: Int, stopWords: StopWords): Boolean {
        var count = 0
        var prevSib = node.previousSibling()
        while (prevSib != null && count < numSibsToCheck) {
            if (prevSib is Element) {
                val ownText = prevSib.ownText()
                // use regular words not stop words
                if (!ownText.isBlank() && stopWords.statistics(ownText).stopWords.size > 5) {
                    return false
                }
            }
            prevSib = prevSib.previousSibling()
            count += 1
        }
        return true
    }


    private fun hasFewWordNextSiblings(node: Node, numSibsToCheck: Int, stopWords: StopWords): Boolean {
        var count = 0
        var nextSib = node.nextSibling()
        while (nextSib != null && count < numSibsToCheck) {
            if (nextSib is Element) {
                val ownText = nextSib.ownText()
                if (!ownText.isBlank() && stopWords.statistics(ownText).stopWords.size > 5) {
                    return false
                }
            }
            nextSib = nextSib.nextSibling()
            count += 1
        }
        return true
    }
}
