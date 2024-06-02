package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

fun String.cleanse(): String {
    return this.trim()
        .replace("null", "")
        .replace("""[\r\n\t]""".toRegex(), " ")
        .replace("""\s\s+""".toRegex(), " ")
        .replace("""<!--.+?-->""", "")
        .replace("""�""", "")
}

fun Element.select(vararg cssQueries: String): Elements {
    return this.select(cssQueries.joinToString(","))
}
