package io.github.cdimascio.essence

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.cdimascio.essence.model.Link
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class EssenceSpec {

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
    fun getsCleanedTextMcSweeneys() {
        checkFixture(site = "mcsweeney", fields = listOf("cleaned_text", "title", "link", "description", "lang", "favicon"))
    }

    @Test
    fun getsCleanedTextCnn() {
        checkFixture(site = "cnn1", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextCnn2() {
        checkFixture(site = "cnn2", fields = listOf("cleaned_text", "description"))
    }

    @Test
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
    fun getsCleanedTextYahoo() {
        checkFixture(site = "yahoo", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextPolitico() {
        checkFixture(site = "politico", fields = listOf("cleaned_text"))
    }

    @Test
    fun getsCleanedTextGooseRegressions() {
        checkFixture(site = "issue4", fields = listOf("cleaned_text"))
        checkFixture(site = "issue24", fields = listOf("cleaned_text"))
        checkFixture(site = "issue25", fields = listOf("cleaned_text"))
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
            .substring(0, newText.length.coerceAtMost(originalText.length))
    }

    private fun cleanOrigText(text: String): String {
        return text.replace("""\n\n""", " ")
    }

    private fun checkFixture(site: String, fields: List<String>) {
        val html = readFileFull("./fixtures/test_$site.html")
        val orig =
            jacksonObjectMapper().readTree(readFileFull("./fixtures/test_$site.json"))
        val data = Essence.extract(html)

        val expected = orig["expected"]
        fields.forEach { field ->
            when (field) {
                "title" -> {
                    assertEquals(expected["title"].asText(), data.title)
                }

                "cleaned_text" -> {
                    val expected = cleanOrigText(expected["cleaned_text"].asText())
                    val actual = cleanTestingTest(data.text, expected)
                    assertNotEquals("text should not be null", "", actual)

                    println(expected)
                    println(actual)
                    assertTrue(data.text.length >= expected.length)

                    assertEquals(expected, actual)
                }

                "link" -> {
                    assertEquals(
                        expected["final_url"].asText(),
                        data.canonicalLink
                    )
                }

                "image" -> {
                    assertEquals(expected["image"].asText().trim(), data.image)
                }

                "description" -> {
                    assertEquals(
                        expected["meta_description"].asText(),
                        data.description
                    )
                }

                "lang" -> {
                    assertEquals(expected["meta_lang"].asText(), data.language)
                }

                "keywords" -> {
                    assertEquals(
                        expected["meta_keywords"].asText(),
                        data.keywords
                    )
                }

                "favicon" -> {
                    assertEquals(
                        expected["meta_favicon"].asText(),
                        data.favicon
                    )
                }

                "tags" -> {
                    val tags = data.tags.sorted()
                    val expectedTags =
                        expected["tags"]?.map { it.asText() }?.sorted()
                            ?: emptyList()
                    expectedTags.zip(tags).forEach { (expected, actual) ->
                        assertEquals(expected, actual)
                    }
                }

                "links" -> {
                    val links = data.links.sortedBy { it.text }
                    val expectedLinks = expected["links"]?.map {
                        Link(
                            it["href"].asText(),
                            it["text"].asText()
                        )
                    }
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
