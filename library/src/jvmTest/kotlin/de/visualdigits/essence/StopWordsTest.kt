package de.visualdigits.essence

import de.visualdigits.essence.words.StopWords
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StopWordsTest {

    @Test
    fun countStopwords() = runTest {
        val stopwords = StopWords.load(Language.en)
        val stats = stopwords.statistics("this is silly")
        assertEquals(3, stats.wordCount)
        assertEquals(2, stats.stopWords.size)
        stats.stopWords.containsAll(listOf("this", "is"))
    }

    @Test
    fun stripsPunctuation() = runTest {
        val stopwords = StopWords.load(Language.en)
        val stats = stopwords.statistics("this! is?? silly....")
        assertEquals(3, stats.wordCount)
        assertEquals(2, stats.stopWords.size)
        stats.stopWords.containsAll(listOf("this", "is"))
    }

    @Test
    fun defaultsToEnglish() = runTest {
        val stopwords = StopWords.load(Language.en)
        val stats = stopwords.statistics("this is fun")
        assertEquals(3, stats.wordCount)
        assertEquals(2, stats.stopWords.size)
        stats.stopWords.containsAll(listOf("this", "is"))
    }

    @Test
    fun handlesSpanish() = runTest {
        val stopwords = StopWords.load(Language.es)
        val stats = stopwords.statistics("este es rico")
        assertEquals(3, stats.wordCount)
        assertEquals(2, stats.stopWords.size)
        stats.stopWords.containsAll(listOf("este", "es"))
    }
}
