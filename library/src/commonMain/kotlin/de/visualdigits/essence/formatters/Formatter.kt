package de.visualdigits.essence.formatters

import com.fleeksoft.ksoup.nodes.Element

fun interface Formatter {

    fun format(element: Element?): String
}
