package de.visualdigits.essence.extractors

import com.fleeksoft.ksoup.nodes.Element
import de.visualdigits.essence.Link
import de.visualdigits.essence.util.find

internal object LinksExtractor {
    fun extract(parentNode: Element?): List<Link> {
        val gatherLinks = { node: Element ->
            node.find("a").fold(mutableListOf<Link>()) { links, childNode ->
                val href = childNode.attr("href").cleanse()
                val text = childNode.text().cleanse() // or html
                if (href.isNotBlank() && text.isNotBlank()) {
                    links += Link(href, text)
                }
                links
            }
        }

        return parentNode?.let { gatherLinks(parentNode) } ?: emptyList()
    }
}

