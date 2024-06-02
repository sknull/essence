package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Element

object ImageExtractor {

    fun extract(document: Element): String {
        return document.select(
            "meta[property='og:image']",
            "meta[property='og:image:url']",
            "meta[itemprop=image]",
            "meta[name='twitter:image:src']",
            "meta[name='twitter:image']",
            "meta[name='twitter:image0']"
        ).first()
            ?.attr("content")
            ?.cleanse()
            ?: ""
    }
}
