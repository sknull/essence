package io.github.cdimascio.essence.extractors

import io.github.cdimascio.essence.formatters.TextFormatter
import io.github.cdimascio.essence.words.StopWords
import org.jsoup.nodes.Element

internal object TextExtractor {
    fun extract(node: Element?, stopWords: StopWords): String {
        return node?.let {
            TextFormatter(stopWords)
                .format(node)
        } ?: ""
    }
}
