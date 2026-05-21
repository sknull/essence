package de.visualdigits.essence

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class GermanPressTest {

    @Test
    fun readTagesschau() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/tagesschau-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/tagesschau-story_expected.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readTagesschau2() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/tagesschau-story-2.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/tagesschau-story_expected2.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readHr1() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/hr-story-1.html").toURI()).readText()
        val result = Essence.extract(html)
        println(result.html?.html())
    }

    @Test
    fun readSpiegel1() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/spiegel-story-1.html").toURI()).readText()
        val result = Essence.extract(html)
        println(result.html?.html())
    }

    @Test
    fun readSpiegel2() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/spiegel-story-2.html").toURI()).readText()
        val result = Essence.extract(html)
        println(result.html?.html())
    }

    @Test
    fun readFocus1() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/focus_story-1.html").toURI()).readText()
        val result = Essence.extract(html)
        println(result.html?.html())
    }

    @Test
    fun readNtv() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ntv-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/ntv-story_expected.htm.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readNdr() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story_expected.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readNdrWithLinks() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story-with-links.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story-with-links_expected.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readNdrWithBold() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story-with-bold.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story-with-bold_expected.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readNdrW3() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story3.html").toURI()).readText()
        val result = Essence.extract(html)
        println(result)
    }

    @Test
    fun readWdr() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/wdr-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/wdr-story_expected.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readHeise() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/heise-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/heise-story_expected.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readT3n() = runTest {
        val html =File(ClassLoader.getSystemResource("germanpress/t3n-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("germanpress/t3n-story_expected.html.txt").toURI()).readText()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }
}
