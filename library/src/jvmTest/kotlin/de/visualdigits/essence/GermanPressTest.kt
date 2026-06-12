package de.visualdigits.essence

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class GermanPressTest {

    @Test
    fun readTagesschau() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/tagesschau-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/tagesschau-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readTagesschau2() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/tagesschau-story-2.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/tagesschau-story_expected2.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readHr1() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/hr-story-1.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/hr-story-1_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readHr2() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/hr-story-2.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/hr-story-2_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readSpiegel1() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/spiegel-story-1.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/spiegel-story-1_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readSpiegel2() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/spiegel-story-2.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/spiegel-story-2_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readFocus1() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/focus_story-1.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/focus_story-1_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readFocus2() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/focus_story-2.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/focus_story-2_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readFocus3() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/focus_story-3.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/focus_story-3_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readNtv() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ntv-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ntv-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readNdr() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readNdrWithLinks() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story-with-links.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story-with-links_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readNdrWithBold() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story-with-bold.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story-with-bold_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readNdr3() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story3.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story3_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readWdr() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/wdr-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/wdr-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readWdrNbsp() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/wdr-story-nbsp.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/wdr-story-nbsp_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readHrNbsp() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/hr-story-nbsp.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/hr-story-nbsp_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readHeise() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/heise-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/heise-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }

    @Test
    fun readT3n() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/t3n-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/t3n-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html?.html())
    }
}

fun File.readTextNormalized(): String {
    return readText().replace("\r\n", "\n").replace("\r", "\n")
}
