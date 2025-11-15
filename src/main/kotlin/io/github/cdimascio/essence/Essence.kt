package io.github.cdimascio.essence

import io.github.cdimascio.essence.cleaners.Cleaner
import io.github.cdimascio.essence.cleaners.ScoreCleaner
import io.github.cdimascio.essence.extractors.AuthorExtractor
import io.github.cdimascio.essence.extractors.CanonicalExtractor
import io.github.cdimascio.essence.extractors.CopyrightExtractor
import io.github.cdimascio.essence.extractors.DataExtractor
import io.github.cdimascio.essence.extractors.DescriptionExtractor
import io.github.cdimascio.essence.extractors.FaviconExtractor
import io.github.cdimascio.essence.extractors.ImageExtractor
import io.github.cdimascio.essence.extractors.KeywordsExtractor
import io.github.cdimascio.essence.extractors.LanguageExtractor
import io.github.cdimascio.essence.extractors.LinksExtractor
import io.github.cdimascio.essence.extractors.PublisherExtractor
import io.github.cdimascio.essence.extractors.SoftTitleExtractor
import io.github.cdimascio.essence.extractors.TagsExtractor
import io.github.cdimascio.essence.extractors.TitleExtractor
import io.github.cdimascio.essence.formatters.HtmlFormatter
import io.github.cdimascio.essence.formatters.TextFormatter
import io.github.cdimascio.essence.scorers.DocumentScorer
import io.github.cdimascio.essence.words.StopWords
import org.jsoup.Jsoup

class Essence(
    private val html: String,
    language: Language? = null
) {

    companion object {

        fun extract(html: String, language: Language? = null): EssenceResult = Essence(html, language).parse()
    }

    private val document = Jsoup.parse(this.html)
    private val language = language ?: Language.from(
        LanguageExtractor.extract(
            document
        )
    )
    private val stopWords = StopWords.load(this.language)
    private val scorer = DocumentScorer(stopWords)
    private val scoredCleaner = ScoreCleaner(stopWords)
    private val textFormatter = TextFormatter(stopWords)
    private val htmlFormatter = HtmlFormatter(stopWords)

    fun parse(): EssenceResult {
        val title = TitleExtractor.extract(document.clone())
        val softTitle = SoftTitleExtractor.extract(document.clone())
        val description = DescriptionExtractor.extract(document.clone())
        val authors = AuthorExtractor.extract(document.clone())
        val copyright = CopyrightExtractor.extract(document.clone())
        val date = DataExtractor.extract(document.clone())
        val favicon = FaviconExtractor.extract(document.clone())
        val publisher = PublisherExtractor.extract(document.clone())
        val image = ImageExtractor.extract(document.clone())
        val tags = TagsExtractor.extract(document.clone())
        val canonicalLink = CanonicalExtractor.extract(document.clone())
        val keywords = KeywordsExtractor.extract(document.clone())

        // clean and score document before extracting text, links and video
        val doc = Cleaner().clean(document.clone())
        val node = scorer.score(doc.clone())

        val topNodeText = node?.clone()?.let { n -> scoredCleaner.clean(n) }
        val links = topNodeText?.clone()?.let { tn -> LinksExtractor.extract(tn) }?:listOf()
        val text = topNodeText?.clone()?.let { tn -> textFormatter.format(tn) }?:""

        val topNodeHtml = node?.clone()?.let { n -> scoredCleaner.cleanHtml(n) }
        val html = topNodeHtml?.clone()?.let { tn -> htmlFormatter.format(tn) } ?: ""

        return EssenceResult(
            authors = authors,
            title = title,
            softTitle = softTitle,
            description = description,
            publisher = publisher,
            date = date,
            copyright = copyright,
            language = language.name,
            text = text,
            html = html,
            favicon = favicon,
            image = image,
            links = links,
            canonicalLink = canonicalLink,
            keywords = keywords,
            tags = tags
        )
    }
}
