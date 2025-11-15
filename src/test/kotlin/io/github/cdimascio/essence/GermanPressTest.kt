package io.github.cdimascio.essence

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class GermanPressTest {

    @Test
    fun readTagesschau() {
        val html = File(ClassLoader.getSystemResource("germanpress/tagesschau-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/tagesschau-story_expected.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readNtv() {
        val html = File(ClassLoader.getSystemResource("germanpress/ntv-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/ntv-story_expected.htm.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readNdr() {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story_expected.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readWdr() {
        val html = File(ClassLoader.getSystemResource("germanpress/wdr-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/wdr-story_expected.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readHeise() {
        val html = File(ClassLoader.getSystemResource("germanpress/heise-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/heise-story_expected.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readT3n() {
        val html =File(ClassLoader.getSystemResource("germanpress/t3n-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/t3n-story_expected.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }
}
