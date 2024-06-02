package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Element

object CanonicalExtractor {

    fun extract(element: Element): String {
        return element
            .selectFirst("link[rel=canonical]")
            ?.attr("href")
            ?.cleanse()
            ?: ""
    }
}
