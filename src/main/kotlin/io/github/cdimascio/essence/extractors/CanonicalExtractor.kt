package io.github.cdimascio.essence.extractors

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element

internal object CanonicalExtractor {
    fun extract(doc: Document): String {
        val tag: Element? = doc.selectFirst("link[rel=canonical]")
        return tag?.attr("href")?.cleanse() ?: ""
    }
}
