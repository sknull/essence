package de.visualdigits.essence.extractors

import com.fleeksoft.ksoup.nodes.Document

internal object KeywordsExtractor {
    fun extract(doc: Document): String {
        return doc.selectFirst("""meta[name=keywords]""")?.attr("content")?.cleanse() ?: ""
    }
}
