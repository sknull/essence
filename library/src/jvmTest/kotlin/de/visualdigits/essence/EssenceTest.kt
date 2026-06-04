package de.visualdigits.essence

import com.fleeksoft.ksoup.helper.Validate.fail
import de.visualdigits.essence.model.Expectations
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File


class EssenceTest {

    @Test
    fun readsFavicon() {
        checkFixture(site = "aolNews", fields = listOf("favicon"))
    }

    @Test
    fun readsDescription() {
        checkFixture("allnewlyrics1", listOf("description"))
    }

    @Test
    fun readsOpenGraphDescription() {
        checkFixture("twitter", listOf("description"))
    }

    @Test
    fun readsKeywords() {
        checkFixture("allnewlyrics1", listOf("keywords"))
    }

    @Test
    fun readsLang() {
        checkFixture("allnewlyrics1", listOf("lang"))
    }

    @Test
    fun readsCanonicalLinks() {
        checkFixture("allnewlyrics1", listOf("link"))
    }

    @Test
    fun readTags() {
        checkFixture("tags_kexp", listOf("tags"))
        checkFixture("tags_deadline", listOf("tags"))
        checkFixture("tags_cnet", listOf("tags"))
        checkFixture("tags_abcau", listOf("tags"))
    }

    @Test
    @Disabled
    fun links() {
        checkFixture("theverge1", listOf("links"))
        checkFixture("techcrunch1", listOf("links"))
        checkFixture("polygon", listOf("links"))
    }

    @Test
    fun images() {
        checkFixture("aolNews", listOf("image"))
        checkFixture("polygon", listOf("image"))
        checkFixture("theverge1", listOf("image"))
    }

    @Test
    fun getsCleanedTextPolygon() {
        checkFixture("polygon", listOf("cleaned_text", "title", "link", "description", "lang", "favicon"))
    }

    @Test
    fun getsCleanedTextTheVerge() {
        checkFixture("theverge1", listOf("cleaned_text", "title", "link", "description", "lang", "favicon"))
    }

    @Test
    @Disabled("somehow broken")
    fun getsCleanedTextMcSweeneys() {
        checkFixture(site = "mcsweeney", fields = listOf("cleaned_text", "title", "link", "description", "lang", "favicon"))
    }

    @Test
    fun getsCleanedTextCnn() {
        checkFixture(site = "cnn1", fields = listOf("cleaned_text"))
    }

    @Test
    @Disabled("somehow broken")
    fun getsCleanedTextCnn2() {
        checkFixture(site = "cnn2", fields = listOf("cleaned_text", "description"))
    }

    @Test
    @Disabled("somehow broken")
    fun getsCleanedTextCnn3() {
        checkFixture(site = "cnn3", fields = listOf("cleaned_text", "description"))
    }

    @Test
    fun getsCleanedTextMSN() {
        checkFixture(site = "msn1", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextTime() {
        checkFixture(site = "time2", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextBusinessWeek() {
        checkFixture(site = "businessWeek1", fields = listOf("cleaned_text"))
        checkFixture(site = "businessWeek2", fields = listOf("cleaned_text"))
        checkFixture(site = "businessWeek3", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextElPais() {
        checkFixture(site = "elpais", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextTechcrunch() {
        checkFixture(site = "techcrunch1", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextFoxNews() {
        checkFixture(site = "foxNews", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextHuffingtonPost() {
        checkFixture(site = "huffingtonPost2", fields = listOf("cleaned_text"))
        checkFixture(site = "testHuffingtonPost", fields = listOf("cleaned_text", "description", "title"))
    }

    @Test
    fun getsCleanedTextESPN() {
        checkFixture(site = "espn", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextTime2() {
        checkFixture(site = "time", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextCNET() {
        checkFixture(site = "cnet", fields = listOf("cleaned_text"))
    }

//    @Test
//    fun getsCleanedTextSch() {
//        checkFixture(site = "sch1", fields = listOf("cleaned_text"))
//    }

    @Test
    fun getsCleanedTextKeras() {
        checkFixture(site = "keras", fields = listOf("cleaned_text"))
    }

    @Test
    @Disabled("somehow broken")
    fun getsCleanedTextYahoo() {
        checkFixture(site = "yahoo", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextPolitico() {
        checkFixture(site = "politico", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextGooseRegressions1() {
        checkFixture(site = "issue4", fields = listOf("cleaned_text"))
        checkFixture(site = "issue24", fields = listOf("cleaned_text"))
        checkFixture(site = "issue25", fields = listOf("cleaned_text"))
    }

    @Test
    @Disabled("somehow broken")
    fun getsCleanedTextGooseRegressions2() {
        checkFixture(site = "issue28", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextGizmodo() {
        checkFixture(site = "gizmodo1", fields = listOf("cleaned_text", "description", "keywords"))
    }

    @Test
    fun getsCleanedTextMashable() {
        checkFixture(site = "mashable_issue_74", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextUSAToday() {
        checkFixture(site = "usatoday_issue_74", fields = listOf("cleaned_text"))
        checkFixture(site = "usatoday1", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextdCurtis() {
        checkFixture(site = "dcurtis", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTagsTheVerge() {
        checkFixture(site = "theverge2", fields = listOf("tags"))
    }

    private fun cleanTestingTest(newText: String, originalText: String): String {
        return newText.replace("""\n\n""", " ").replace("""\ \ """, " ")
            .substring(0, Math.min(newText.length, originalText.length))
    }

    private fun cleanOrigText(text: String): String {
        return text.replace("""\n\n""", " ")
    }

    fun checkFixture(site: String, fields: List<String>) = runTest {
        val html = File(ClassLoader.getSystemResource("./fixtures/test_$site.html").toURI()).readLines()
            .joinToString<String>(" ")
        val orig = Json.decodeFromString<Expectations>(File(ClassLoader.getSystemResource("./fixtures/test_$site.json").toURI()).readText())
        val data = Essence.extract(html)

        val expected = orig.expected
        for (field in fields) {
            when (field) {
                "title" -> {
                    assertEquals(expected.title, data.title)
                }
                "cleaned_text" -> {
                    val origText = cleanOrigText(expected.cleanedText?:"")
                    val newText = cleanTestingTest(data.text, origText)
                    assertNotEquals("text should not be null", "", newText)

                    println("origText: >$origText<")
                    println("newText: >$newText<")
                    val dataTextLength = data.text.length
                    val origLength = origText.length
//                    assertTrue(dataTextLength >= origLength)

                    assertEquals(origText, newText)
                }
                "link" -> {
                    assertEquals(expected.finalUrl, data.canonicalLink)
                }
                "image" -> {
                    assertEquals(expected.image?.trim(), data.image)
                }
                "description" -> {
                    assertEquals(expected.metaDescription, data.description)
                }
                "lang" -> {
                    assertEquals(expected.metaLang, data.language)
                }
                "keywords" -> {
                    assertEquals(expected.metaKeywords, data.keywords)
                }
                "favicon" -> {
                    assertEquals(expected.metaFavicon, data.favicon)
                }
                "tags" -> {
                    val tags = data.tags.sorted()
                    val expectedTags = expected.tags?.map { it as String }?.sorted() ?: emptyList()
                    expectedTags.zip(tags).forEach { (expected, actual) ->
                        assertEquals(expected, actual)
                    }
                }
                "links" -> {
                    val links = data.links.sortedBy { it.text }
                    val expectedLinks = expected.links.map { Link(it.href, it.text) }
                        ?: emptyList()
                    links.zip(expectedLinks).forEach { (actual, expected) ->
                        assertEquals(expected.text, actual.text)
                        assertEquals(expected.href, actual.href)
                    }
                }
                "videos" -> {
//                    assertEquals(expected["keywords"], data.keywords)
                    fail("videos not implemented")
                }
                else -> {
                    fail("invalid test")
                }
            }
        }
    }
}
