package de.visualdigits.essence.formatters

import com.fleeksoft.ksoup.nodes.Element
import de.visualdigits.essence.util.find

abstract class Formatter {

    abstract fun format(element: Element?): String

    fun Element.removeNegativeScoredNodes() {
        val gravityElements = find("*[gravityScore]")
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
}
