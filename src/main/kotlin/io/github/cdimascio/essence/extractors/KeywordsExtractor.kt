package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Element

object KeywordsExtractor {

    fun extract(document: Element): String {
        return document
            .selectFirst("""meta[name=keywords]""")
            ?.attr("content")
            ?.cleanse()
            ?: ""
    }
}
