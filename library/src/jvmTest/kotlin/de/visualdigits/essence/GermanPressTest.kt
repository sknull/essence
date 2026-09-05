package de.visualdigits.essence

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class GermanPressTest {

    @Test
    fun readArd() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ard-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ard-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readTagesschau() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/tagesschau-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/tagesschau-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readTagesschau2() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/tagesschau-story-2.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/tagesschau-story-2_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readTagesschau3() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/tagesschau-story-3.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/tagesschau-story-3_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readTagesschau4() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/tagesschau-story-4.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/tagesschau-story-4_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readHr1() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/hr-story-1.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/hr-story-1_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readHr2() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/hr-story-2.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/hr-story-2_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readSpiegel1() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/spiegel-story-1.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/spiegel-story-1_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readSpiegel2() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/spiegel-story-2.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/spiegel-story-2_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readFocus1() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/focus-story-1.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/focus-story-1_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readFocus2() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/focus-story-2.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/focus-story-2_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readFocus3() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/focus-story-3.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/focus-story-3_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readFocus4() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/focus-story-4.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/focus-story-4_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readFocus5() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/focus-story-5.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/focus-story-5_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readNtv() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ntv-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ntv-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readNtv2() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ntv-story-2.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ntv-story-2_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readNdr() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readNdrWithLinks() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story-with-links.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story-with-links_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readNdrWithBold() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story-with-bold.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story-with-bold_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readNdr3() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story-3.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story-3_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readNdr4() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story-4.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story-4_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readNdr5() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/ndr-story-5.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/ndr-story-5_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readWdr() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/wdr-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/wdr-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readWdr2() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/wdr-story-2.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/wdr-story-2_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readWdr3() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/wdr-story-3.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/wdr-story-3_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readWdr4() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/wdr-story-4.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/wdr-story-4_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readWdrNbsp() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/wdr-story-nbsp.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/wdr-story-nbsp_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readHrNbsp() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/hr-story-nbsp.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/hr-story-nbsp_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readHeise() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/heise-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/heise-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readT3n() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/t3n-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/t3n-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }

    @Test
    fun readZdf() = runTest {
        val html = File(ClassLoader.getSystemResource("germanpress/zdf-story.html").toURI()).readTextNormalized()
        val expected = File(ClassLoader.getSystemResource("germanpress/zdf-story_expected.html.txt").toURI()).readTextNormalized()
        val result = Essence.extract(html)
        assertEquals(expected, result.html)
    }
}

fun File.readTextNormalized(): String {
    return readText().replace("\r\n", "\n").replace("\r", "\n")
}
