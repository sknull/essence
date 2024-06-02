package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Element

object DescriptionExtractor {

    fun extract(element: Element): String {
        return element.select(
           "meta[name=description]",
           "meta[property='og:description']"
        ).first()
            ?.attr("content")
            ?.cleanse()
            ?: ""
    }
}
