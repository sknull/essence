package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Document

private val REGEX_COPYRIGHT = """.*?©(\s*copyright)?([^,;:.|\r\n]+).*""".toRegex(RegexOption.IGNORE_CASE)

object CopyrightExtractor {

    fun extract(document: Document): String {
        var text = document.select(
            "p[class*='copyright']",
            "div[class*='copyright']",
            "span[class*='copyright']",
            "li[class*='copyright']",
            "p[id*='copyright']",
            "div[id*='copyright']",
            "span[id*='copyright']",
            "li[id*='copyright']"
        ).first()
            ?.text()
            ?: ""
        if (text.isBlank()) {
            val bodyText = document
                .body()
                .text()
                .replace("""s*[\r\n]+\s*""".toRegex(), ". ")
            if (bodyText.contains("©")) {
                text = bodyText
            }
        }
        val match = REGEX_COPYRIGHT.find(text)
        val copyright = match?.groupValues?.get(2) ?: ""
        return copyright.cleanse()
    }
}

