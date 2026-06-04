package de.visualdigits.essence

import com.fleeksoft.ksoup.Ksoup
import de.visualdigits.essence.formatters.TextFormatter
import de.visualdigits.essence.words.StopWords
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class FormatterTest {

    @Test
    fun replacesLinksWithPlainText() = runTest {
        val stopWords = StopWords.load(Language.en)
        val formatter = TextFormatter(stopWords)

        val contents = File(ClassLoader.getSystemResource("./fixtures/test_businessWeek1.html").toURI()).readLines()
            .joinToString<String>(" ")
        val doc = Ksoup.parse(contents)

        val originalLinks = doc.select("a")
        assertEquals(232, originalLinks.size)

        formatter.format(doc)

        val links = doc.select("a")
        assertEquals(0, links.size)
    }

    @Test
    fun doesNotDropTextNodesAccidentally() = runTest {
        val stopWords = StopWords.load(Language.en)
        val formatter = TextFormatter(stopWords)

        val contents = File(ClassLoader.getSystemResource("./fixtures/test_wikipedia1.html").toURI()).readLines()
            .joinToString<String>(" ")
        val doc = Ksoup.parse(contents)

        formatter.format(doc)

        assertTrue(doc.html().contains("""
            is a thirteen episode anime series directed by Akitaro Daichi and written by Hideyuki Kurata
            """.trimIndent()))
    }
}
