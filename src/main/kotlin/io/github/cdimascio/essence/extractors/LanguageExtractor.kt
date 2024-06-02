package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Element
import java.util.*

object LanguageExtractor {

    fun extract(document: Element): String {
        var lang = document
            .select("html")
            .attr("lang")
        if (lang.isBlank()) {
            lang = document.select(
                    "meta[name=lang]",
                    "meta[http-equiv=content-language]"
            ).first()
                ?.attr("content")
                ?: ""
        }
        return if (lang.isNotBlank() && lang.length >= 2) {
            // return the first 2 letter ISO lang code with no country
            lang
                .cleanse()
                .substring(0, 2)
                .lowercase(Locale.getDefault())
        } else {
            ""
        }
    }
}
