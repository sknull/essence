package de.visualdigits.essence

import com.fleeksoft.ksoup.Ksoup
import de.visualdigits.essence.formatters.TextFormatter
import de.visualdigits.essence.words.StopWords
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FormatterTest {

    private val stopWords = StopWords.load(Language.en)
    private val formatter = TextFormatter(stopWords)

    @Test
    fun replacesLinksWithPlainText() {
        val contents = readFileFull("./fixtures/test_businessWeek1.html")
        val doc = Ksoup.parse(contents)

        val originalLinks = doc.select("a")
        assertEquals(232, originalLinks.size)

        formatter.format(doc)

        val links = doc.select("a")
        assertEquals(0, links.size)
    }

    @Test
    fun doesNotDropTextNodesAccidentally() {
        val contents = readFileFull("./fixtures/test_wikipedia1.html")
        val doc = Ksoup.parse(contents)

        formatter.format(doc)

        assertTrue(doc.html().contains("""
            is a thirteen episode anime series directed by Akitaro Daichi and written by Hideyuki Kurata
            """.trimIndent()))
    }
}
