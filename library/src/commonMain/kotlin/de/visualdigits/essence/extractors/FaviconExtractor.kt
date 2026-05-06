package de.visualdigits.essence.extractors

import com.fleeksoft.ksoup.nodes.Document

internal object FaviconExtractor {
    fun extract(doc: Document): String {
        val favicon = doc.select("link").filter {
            it.attr("rel").lowercase() == "shortcut icon"
        }
        if (favicon.isNotEmpty()) {
            return favicon[0].attr("href")
        }
        return ""
    }
}
