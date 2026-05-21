package de.visualdigits.essence

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import de.visualdigits.essence.cleaners.Cleaner
import de.visualdigits.essence.cleaners.HtmlScoreCleaner
import de.visualdigits.essence.cleaners.TextScoreCleaner
import de.visualdigits.essence.extractors.AuthorExtractor
import de.visualdigits.essence.extractors.CanonicalExtractor
import de.visualdigits.essence.extractors.CopyrightExtractor
import de.visualdigits.essence.extractors.DataExtractor
import de.visualdigits.essence.extractors.DescriptionExtractor
import de.visualdigits.essence.extractors.FaviconExtractor
import de.visualdigits.essence.extractors.ImageExtractor
import de.visualdigits.essence.extractors.KeywordsExtractor
import de.visualdigits.essence.extractors.LanguageExtractor
import de.visualdigits.essence.extractors.LinksExtractor
import de.visualdigits.essence.extractors.PublisherExtractor
import de.visualdigits.essence.extractors.SoftTitleExtractor
import de.visualdigits.essence.extractors.TagsExtractor
import de.visualdigits.essence.extractors.TitleExtractor
import de.visualdigits.essence.formatters.HtmlFormatter
import de.visualdigits.essence.formatters.TextFormatter
import de.visualdigits.essence.scorers.DocumentScorer
import de.visualdigits.essence.words.StopWords
import java.util.UUID

object Essence {

    suspend fun extract(
        html: String,
        language: Language? = null
    ): EssenceResult {
        val document = Ksoup.parse(html = html)
        val nodeMap = createNodeIds(document)
        val language = language ?: Language.from(
            LanguageExtractor.extract(
                document.clone()
            )
        )
        val stopWords = StopWords.load(language)
        val scorer = DocumentScorer(stopWords)
        val textScoredCleaner = TextScoreCleaner(stopWords)
        val htmlScoredCleaner = HtmlScoreCleaner(stopWords)
        val textFormatter = TextFormatter(stopWords)
        val htmlFormatter = HtmlFormatter(stopWords)

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

        val topNodeText = node?.clone()?.let { n -> textScoredCleaner.clean(n) }
        val links = topNodeText?.clone()?.let { tn -> LinksExtractor.extract(tn) }?:listOf()
        val text = topNodeText?.clone()?.let { tn -> textFormatter.format(tn) }?:""

        // lookup top node from original document
        val topNodeHtml = nodeMap[node?.attr("essenceNodeId")]
        val html = topNodeHtml?.clone()?.let { tn -> htmlFormatter.formatElement(tn) }

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

    /**
     * appends a unique id to each source element in the document and returns a map of uuid to element.
     */
    private fun createNodeIds(element: Element, nodeMap: MutableMap<String, Element> = mutableMapOf()): Map<String, Element> {
        val essenceNodeId = UUID.randomUUID().toString()
        nodeMap[essenceNodeId] = element
        element.attr("essenceNodeId", essenceNodeId)
        element.childElementsList().forEach { child -> createNodeIds(child, nodeMap) }
        return nodeMap
    }
}
