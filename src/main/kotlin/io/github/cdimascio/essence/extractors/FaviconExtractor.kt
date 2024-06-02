package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Element
import java.util.*

object FaviconExtractor {

    fun extract(document: Element): String {
        return document
                .select("link")
                .filter { elem ->
                    elem.attr("rel").lowercase(Locale.getDefault()) == "shortcut icon"
                }.firstOrNull()
                ?.attr("href")
            ?:""
    }
}
