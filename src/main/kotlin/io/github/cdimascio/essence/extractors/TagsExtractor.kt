package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Element

object TagsExtractor {

    fun extract(element: Element): List<String> {
        var candidates = element.select("a[rel='tag']")
        if (candidates.isEmpty()) {
            candidates = element.select("""
                a[href*='/tag/'],
                a[href*='/tags/'],
                a[href*='/topic/'],
                a[href*='?keyword=']
            """.trimIndent())
        }

        return candidates.map {
            it.text().cleanse().replace("""[\s\t\n]+""".toRegex(), " ")
        }.filter {
            it.isNotBlank()
        }.distinct()
    }
}
