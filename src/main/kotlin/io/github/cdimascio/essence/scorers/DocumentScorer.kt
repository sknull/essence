package io.github.cdimascio.essence.scorers

import io.github.cdimascio.essence.util.NodeHeuristics
import io.github.cdimascio.essence.util.TraversalHelpers
import io.github.cdimascio.essence.words.StopWords
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow

object DocumentScorer {

    internal fun getScore(node: Node): Double {
        return try {
            node.attr("gravityScore").toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    // TODO: why update the DOM to track score - do seomething else
    internal fun updateScore(node: Node, addToScore: Double) {
        val currentScore = getScore(node)
        val score = currentScore + addToScore
        node.attr("gravityScore", score.toString())
    }

    internal fun updateNodeCount(node: Node, addToCount: Int) {
        val currentCount = try {
            node.attr("gravityNodes").toInt()
        } catch (e: Exception) {
            0
        }
        val count = currentCount + addToCount
        node.attr("gravityNodes", count.toString())
    }

    fun score(document: Document, stopWords: StopWords): Element? {
        val nodesWithText = mutableListOf<Element>()
        val nodesToCheck = document.select("header, h1, h2, h3, p, pre, td")
        nodesToCheck.forEach { node ->
            val text = node.text()
            val wordStats = stopWords.statistics(text)
            val hasHighLinkDensity = NodeHeuristics.hasHighLinkDensity(node)

            var rating = upscaleHeadlines(node.tagName(), wordStats.stopWords.size.toDouble())
            if (rating > 2.0 && !hasHighLinkDensity) {
                nodesWithText.add(node)
            }
//            else {
//                println("### skipping ${node.tagName()}: ${node.text()} - ${wordStats.stopWords} $hasHighLinkDensity")
//            }
        }
        val numNodesWithText = nodesWithText.size
        var startingBoost = 1.0
        val negativeScoring = 0
        val bottomNegativeScoreNodes = numNodesWithText * 0.25
        val parentNodes = mutableSetOf<Element>()

        nodesWithText.forEachIndexed { index, node ->
            var boostScore = 0.0
            // If this node has nearby nodes that contain
            // some good text, give the node some boost points
            if (isBoostable(node, stopWords)) { // && index >= 0) {
                boostScore = (1.0 / startingBoost) * 50
                startingBoost += 1
            }
            if (numNodesWithText > 15 && ((numNodesWithText - index) <= bottomNegativeScoreNodes)) {
                val booster = bottomNegativeScoreNodes - (numNodesWithText - 1)
                boostScore = -1.0 * booster.pow(2.0)
                val negScore = abs(boostScore) + negativeScoring

                if (negScore > 40) {
                    boostScore = 5.0
                }
            }

            // Give the current node a score of how many common words
            // it contains plus any boost
            val text = node.text()
            // Give the current node a score of how many common words
            // it contains plus any boost
            val wordStats = stopWords.statistics(text)
            var upScore = floor(wordStats.stopWords.size + boostScore)
            upScore = upscaleHeadlines(node.tagName(), upScore)

            // THis only goes up 2 levels per node?
            // Propagate the score upwards
            val parent = node.parent()
            updateScore(node, upScore) // also remember score for the node itself to avoid removing afterwards (ScoreCleaner checks for parent child relation)
            updateScore(parent, upScore)
            updateNodeCount(parent, 1)

            if (!parentNodes.contains(parent)) {
                parentNodes.add(parent)
            }
            val grandParent: Node? = parent.parent()

            grandParent?.let {
                updateScore(it, upScore / 2.0)
                updateNodeCount(it, 1)
                if (!parentNodes.contains(grandParent)) {
                    if (grandParent is Element) {
                        parentNodes.add(grandParent)
                    } else {
                        throw java.lang.IllegalStateException("Fix unfluffer code: Didn't think a parent could be anything but an element here")
                    }
                }
            }
        }

        return findTopNode(parentNodes)

    }

    private fun upscaleHeadlines(tagName: String, upScore: Double): Double {
        return if (tagName == "h1" || tagName == "h2" || tagName == "h3") {
            upScore * 4 - "${tagName[1]}".toInt()
        } else {
            upScore
        }
    }

    private fun findTopNode(scoredNodes: Set<Element>): Element? {
        var topNodeScore = 0.0
        var topNode: Element? = null
        // walk each parent and grandparent and find the one that contains the highest sum score
        // of 'texty' child nodes.
        // That's probably our best node!
        scoredNodes.forEach { node ->
            val score = getScore(node)
            if (score > topNodeScore) {
                topNodeScore = score
                topNode = node
            }
            if (topNode == null) {
                topNode = node
            }
        }
        return topNode
    }

    /**
     *  Given a text node, check all previous siblings.
     *  If the sibling node looks 'texty' and isn't too many
     *  nodes away, it's probably some yummy text
     **/
    private fun isBoostable(node: Node, stopWords: StopWords): Boolean {
        val previousSiblings = TraversalHelpers.getAllPreviousElementSiblings(node)
        val MIN_STOP_WORDS_COUNT = 5
        val MAX_STEPS_FROM_NODE = 3
        previousSiblings.forEachIndexed { stepsAway, element ->
            if (element.tagName() == "p") {
                if (stepsAway >= MAX_STEPS_FROM_NODE) {
                    return false
                }
                val text = element.text()
                val stats = stopWords.statistics(text)
                if (stats.stopWords.size > MIN_STOP_WORDS_COUNT) {
                    return true
                }
            }
        }
        return false
    }
}
