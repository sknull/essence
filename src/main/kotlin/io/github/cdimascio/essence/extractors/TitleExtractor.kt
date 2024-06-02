package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Element

object TitleExtractor {

    fun extract(element: Element, soft: Boolean = false): String {
        val title = (element.select("meta[property='og:title']") +
            listOfNotNull(
                element.selectFirst("h1[class*='title']"),
                element.selectFirst("title"),
                element.selectFirst("h1"),
                element.selectFirst("h2")
            ))
            .map { c ->
                listOf(
                    c.attr("content"),
                    c.text()
                ).find { it.isNotBlank() }?.cleanse() ?: ""
            }
            .find { it.isNotBlank() } ?: ""
        return (if (soft) listOf("|", " - ", "»") else listOf("|", " - ", ">>", ":"))
            .find { title.contains(it) }
            ?.let { elem ->         // find the largest substring
                title
                    .split(elem)
                    .map { Pair(it, it.length) }
                    .maxBy { it.second }
                    .first.cleanse()
            }
            ?: title.cleanse()
    }
}
