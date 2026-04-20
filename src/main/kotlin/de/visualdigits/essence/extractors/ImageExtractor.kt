package de.visualdigits.essence.extractors

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element

internal object ImageExtractor {
    fun extract(doc: Document): String {
        val candidate: Element? = doc.selectFirst("""
            meta[property='og:image'],
            meta[property='og:image:url'],
            meta[itemprop=image],
            meta[name='twitter:image:src'],
            meta[name='twitter:image'],
            meta[name='twitter:image0']
        """.trimIndent())
        return candidate?.attr("content")?.cleanse() ?: ""
    }
}
