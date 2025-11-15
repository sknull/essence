package io.github.cdimascio.essence.formatters

import org.jsoup.nodes.Element

fun interface Formatter {

    fun format(node: Element?): String
}
