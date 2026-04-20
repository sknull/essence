package io.github.cdimascio.essence.scorers

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node

typealias ScoredElement = Element

fun interface Scorer {

    fun score(doc: Document): ScoredElement?

    companion object {
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
    }
}
