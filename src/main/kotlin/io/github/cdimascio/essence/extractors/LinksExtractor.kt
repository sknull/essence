package io.github.cdimascio.essence.extractors

import io.github.cdimascio.essence.extractors.cleanse
import io.github.cdimascio.essence.model.Link
import io.github.cdimascio.essence.util.find
import org.jsoup.nodes.Element

object LinksExtractor {

    fun extract(element: Element?): List<Link> {
        return element
            ?.find("a")
            ?.mapNotNull { a ->
                val href = a.attr("href").cleanse()
                val text = a.text().cleanse() // or html
                if (href.isNotBlank()) {
                    if (text.isNotBlank()) {
                        Link(href, text)
                    } else {
                        Link(href, href)
                    }
                } else {
                    null
                }
            }
            ?: listOf()
    }
}

