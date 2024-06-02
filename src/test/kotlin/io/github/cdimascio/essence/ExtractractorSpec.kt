package io.github.cdimascio.essence

import io.github.cdimascio.essence.extractors.AuthorExtractor
import io.github.cdimascio.essence.extractors.CopyrightExtractor
import io.github.cdimascio.essence.extractors.DataExtractor
import io.github.cdimascio.essence.extractors.FaviconExtractor
import io.github.cdimascio.essence.extractors.ImageExtractor
import io.github.cdimascio.essence.extractors.PublisherExtractor
import io.github.cdimascio.essence.extractors.TitleExtractor
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExtractractorSpec {

    @Test
    fun returnsABlankTitle() {
        val html = "<html><head><title></title></head></html>"
        val d = Jsoup.parse(html)
        val title = TitleExtractor.extract(d)
        assertEquals("", title)
    }

    @Test
    fun returnsASimpleTitle() {
        val html = "<html><head><title>This is my page</title></head></html>"
        val d = Jsoup.parse(html)
        val title = TitleExtractor.extract(d)
        assertEquals("This is my page", title)
    }

    @Test
    fun returnsASimpleChunkTitle() {
        val html =
            "<html><head><title>This is my page - mysite</title></head></html>"
        val d = Jsoup.parse(html)
        val title = TitleExtractor.extract(d)
        assertEquals("This is my page", title)
    }

    @Test
    fun returnsSoftTitleWithoutTruncation() {
        val html =
            "<html><head><title>University Budgets: Where Your Fees Go | Top Universities</title></head></html>"
        val d = Jsoup.parse(html)
        val title = TitleExtractor.extract(d, soft = true)
        assertEquals("University Budgets: Where Your Fees Go", title)
    }

    @Test
    fun titlePrefersTheMetaTag() {
        val html =
            "<html><head><title>This is my page - mysite</title><meta property=\"og:title\" content=\"Open graph title\"></head></html>"
        val d = Jsoup.parse(html)
        val title = TitleExtractor.extract(d)
        assertEquals("Open graph title", title)
    }

    @Test
    fun fallsbackToTitleIfEmptyMetatag() {
        val html =
            "<html><head><title>This is my page - mysite</title><meta property=\"og:title\" content=\"\"></head></html>"
        val d = Jsoup.parse(html)
        val title = TitleExtractor.extract(d)
        assertEquals("This is my page", title)
    }

    @Test
    fun returnsAnotherSimpleTitleChunk() {
        val html =
            "<html><head><title>coolsite.com: This is my page</title></head></html>"
        val d = Jsoup.parse(html)
        val title = TitleExtractor.extract(d)
        assertEquals("This is my page", title)
    }

    @Test
    fun returnsAnotherSimpleTitleChunkWithoutJunk() {
        val html =
            "<html><head><title>coolsite.com: &#65533; This&#65533; is my page</title></head></html>"
        val d = Jsoup.parse(html)
        val title = TitleExtractor.extract(d)
        assertEquals("This is my page", title)
    }

    @Test
    fun returnsTheFirstTitle() {
        val html =
            "<html><head><title>This is my page</title></head><svg xmlns=\"http://www.w3.org/2000/svg\"><title>svg title</title></svg></html>"
        val d = Jsoup.parse(html)
        val title = TitleExtractor.extract(d)
        assertEquals("This is my page", title)
    }

    @Test
    fun missingFavicon() {
        val html = "<html><head><title></title></head></html>"
        val d = Jsoup.parse(html)
        val favicon = FaviconExtractor.extract(d)
        assertEquals("", favicon)
    }

    @Test
    fun returnsTheArticlePublishedMetaDate() {
        val html =
            "<html><head><meta property=\"article:published_time\" content=\"2014-10-15T00:01:03+00:00\" /></head></html>"
        val d = Jsoup.parse(html)
        val date = DataExtractor.extract(d)
        assertEquals("2014-10-15T00:01:03+00:00", date)
    }

    @Test
    fun returnsTheArticleDublinCoreMetaDate() {
        val html =
            "<html><head><meta name=\"DC.date.issued\" content=\"2014-10-15T00:01:03+00:00\" /></head></html>"
        val d = Jsoup.parse(html)
        val date = DataExtractor.extract(d)
        assertEquals("2014-10-15T00:01:03+00:00", date)
    }

    @Test
    fun returnsTheDateInTheTimeElement() {
        val html =
            "<html><head></head><body><time>24 May, 2010</time></body></html>"
        val d = Jsoup.parse(html)
        val date = DataExtractor.extract(d)
        assertEquals("24 May, 2010", date)
    }

    @Test
    fun returnsTheDateInTheTimeElementDatetimeAttribute() {
        val html =
            "<html><head></head><body><time datetime=\"2010-05-24T13:47:52+0000\">24 May, 2010</time></body></html>"
        val d = Jsoup.parse(html)
        val date = DataExtractor.extract(d)
        assertEquals("2010-05-24T13:47:52+0000", date)
    }

    @Test
    fun returnsNothingIfDateEqualsNull() {
        val html =
            "<html><head><meta property=\"article:published_time\" content=\"null\" /></head></html>"
        val d = Jsoup.parse(html)
        val date = DataExtractor.extract(d)
        assertEquals("", date)
    }

    @Test
    fun returnsTheCopyrightLinElement() {
        val html =
            "<html><head></head><body><div>Some stuff</div><ul><li class='copyright'><!-- // some garbage -->© 2016 The World Bank Group, All Rights Reserved.</li></ul></body></html>"
        val d = Jsoup.parse(html)
        val copyright = CopyrightExtractor.extract(d)
        assertEquals("2016 The World Bank Group", copyright)
    }

    @Test
    fun returnsTheCopyrightFoundInText() {
        val html =
            "<html><head></head><body><div>Some stuff</div><ul>© 2016 The World Bank Group, All Rights Reserved\nSome garbage following</li></ul></body></html>"
        val d = Jsoup.parse(html)
        val copyright = CopyrightExtractor.extract(d)
        assertEquals("2016 The World Bank Group", copyright)
    }

    @Test
    fun returnsNothingIfNoCopyrightInText() {
        val html =
            "<html><head></head><body><div>Some stuff</div><ul>© 2016 The World Bank Group, All Rights Reserved\nSome garbage following</li></ul></body></html>"
        val d = Jsoup.parse(html)
        val copyright = CopyrightExtractor.extract(d)
        assertEquals("2016 The World Bank Group", copyright)
    }

    @Test
    fun returnsTheArticlePublishedMetaAuthor() {
        val html =
            "<html><head><meta property=\"article:author\" content=\"Joe Bloggs\"/></head></html>"
        val d = Jsoup.parse(html)
        val authors = AuthorExtractor.extract(d)
        assertTrue(authors.contains("Joe Bloggs"))
    }

    @Test
    fun returnsTheMetaAuthor() {
        val html =
            "<html><head><meta property=\"article:author\" content=\"Sarah Smith\" /><meta name=\"author\" content=\"Joe Bloggs\" /></head></html>"
        val d = Jsoup.parse(html)
        val authors = AuthorExtractor.extract(d)
        assertTrue(authors.contains("Joe Bloggs") && authors.contains("Sarah Smith"))
    }

    @Test
    fun returnsTheNamedAuthorInTheTextAsFallback() {
        val html =
            "\"<html><head></head><body><span class=\"author\"><a href=\"/author/gary-trust-6318\" class=\"article__author-link\">Gary Trust</a></span></body></html>\""
        val d = Jsoup.parse(html)
        val authors = AuthorExtractor.extract(d)
        assertTrue(authors.contains("Gary Trust"))
    }

    @Test
    fun returnsTheMetaAuthorButIgnoreNullValue() {
        val html =
            """"<html><head><meta property="article:author" content="null" /><meta name="author" content="Joe Bloggs" /></head></html>""""
        val d = Jsoup.parse(html)
        val authors = AuthorExtractor.extract(d)
        assertTrue(authors.contains("Joe Bloggs"))
        assertEquals(1, authors.size)
        assertTrue(!authors.contains("null"))
    }

    @Test
    fun returnsTheMetaPublisher() {
        val html =
            """"<html><head><meta property="og:site_name" content="Polygon" /><meta name="author" content="Griffin McElroy" /></head></html>"""
        val d = Jsoup.parse(html)
        val publisher = PublisherExtractor.extract(d)
        assertEquals("Polygon", publisher)
    }

    @Test
    fun returnsNothingIfPublisherEqualsNull() {
        val html =
            """<html><head><meta property="og:site_name" content="null" /></head></html>"""
        val d = Jsoup.parse(html)
        val publisher = PublisherExtractor.extract(d)
        assertEquals("", publisher)
    }

    @Test
    fun returnNothingIfImageEqualsNull() {
        val html =
            """<html><head><meta property="og:image" content="null" /></head></html>"""
        val d = Jsoup.parse(html)
        val image = ImageExtractor.extract(d)
        assertEquals("", image)
    }
}

