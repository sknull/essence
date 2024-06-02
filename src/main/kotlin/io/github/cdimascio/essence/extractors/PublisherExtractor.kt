package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Element

object PublisherExtractor {

    fun extract(document: Element): String {
        return document.select(
            "meta[property='og:site_name']",
            "meta[name='dc.publisher']",
            "meta[name='DC.publisher']",
            "meta[name='DC.Publisher']"
        ).first()?.let { c ->
            val text = c.attr("content").cleanse()
            if (text.isNotBlank()) {
                text.cleanse()
            } else {
                ""
            }
        }?:""
    }
}
