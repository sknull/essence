package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Element


object AuthorExtractor {

    fun extract(element: Element): List<String> {
        val authors = element.select(
            "meta[property='article:author']",
            "meta[property='og:article:author']",
            "meta[name='author']",
            "meta[name='dcterms.creator']",
            "meta[name='DC.creator']",
            "meta[name='DC.Creator']",
            "meta[name='dc.creator']",
            "meta[name='creator']"
        ).fold(mutableListOf<String>()) { l, c ->
            val author = c.attr("content").cleanse()
            if (author.isNotBlank()) {
                l.add(author)
            }
            l
        }

        if (authors.isEmpty()) {
            element.select(
                    "span[class*='author']",
                    "p[class*='author']",
                    "div[class*='author']",
                    "span[class*='byline']",
                    "p[class*='byline']",
                    "div[class*='byline']"
            ).first()
                ?.let {
                    authors.add(it.text().cleanse())
            }
        }

        return authors
    }
}
