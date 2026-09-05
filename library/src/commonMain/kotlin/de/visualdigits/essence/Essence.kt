package de.visualdigits.essence

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
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
import de.visualdigits.essence.model.ElementType
import de.visualdigits.essence.model.EssenceResult
import de.visualdigits.essence.model.HtmlPart
import de.visualdigits.essence.model.Language
import de.visualdigits.essence.model.Part
import de.visualdigits.essence.scorers.DocumentScorer
import de.visualdigits.essence.words.StopWords
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object Essence {

    fun extract(
        html: String,
        language: Language? = null
    ): EssenceResult {
        val document = Ksoup.parse(html = html)
        val lang = language ?: Language.from(LanguageExtractor.extract(document))

        val essenceResult = extractText(document.clone(), lang)
        val parts = extractHtml(document, lang)

        val resultHtml = parts
            .filter { it.html.isNotEmpty() }
            .joinToString("\n") { part ->
                if (part is HtmlPart && part.elementType == ElementType.div) {
                    val elem = Element("div")
                    elem.addChildren(*part.html.toTypedArray())
                    elem.outerHtml()
                } else {
                    part.html.joinToString("\n") { elem ->
                        elem.outerHtml()
                    }
                }
            }
        return essenceResult.copy(
            language = lang.name,
            html = resultHtml,
            parts = parts
        )
    }

    private fun extractText(
        document: Document,
        language: Language
    ): EssenceResult {
        val stopWords = StopWords.load(language)
        val scorer = DocumentScorer(stopWords)
        val textScoredCleaner = TextScoreCleaner(stopWords)
        val textFormatter = TextFormatter(stopWords)

        val title = TitleExtractor.extract(document)
        val softTitle = SoftTitleExtractor.extract(document)
        val description = DescriptionExtractor.extract(document)
        val authors = AuthorExtractor.extract(document)
        val copyright = CopyrightExtractor.extract(document)
        val date = DataExtractor.extract(document)
        val favicon = FaviconExtractor.extract(document)
        val publisher = PublisherExtractor.extract(document)
        val image = ImageExtractor.extract(document)
        val tags = TagsExtractor.extract(document)
        val canonicalLink = CanonicalExtractor.extract(document)
        val keywords = KeywordsExtractor.extract(document)

        // clean and score document before extracting text, links and video
        val doc = Cleaner().clean(document)
        val node = scorer.score(doc)

        val topNodeText = node?.let { n -> textScoredCleaner.clean(n) }
        val links = topNodeText?.let { tn -> LinksExtractor.extract(tn) }?:listOf()
        val text = topNodeText?.let { tn -> textFormatter.format(tn) }?:""

        return EssenceResult(
            text = text,
            authors = authors,
            title = title,
            softTitle = softTitle,
            copyright = copyright,
            date = date,
            publisher = publisher,
            description = description,
            favicon = favicon,
            image = image,
            links = links,
            canonicalLink = canonicalLink,
            keywords = keywords,
            tags = tags
        )
    }

    private fun extractHtml(
        document: Document,
        language: Language
    ): List<Part> {
        val nodeMap = createNodeIds(document)
        val stopWords = StopWords.load(language)
        val htmlScoredCleaner = HtmlScoreCleaner(stopWords)
        val htmlFormatter = HtmlFormatter()

        // clean and score document before extracting text, links and video
        val doc = Cleaner().clean(document.clone())
        val node = DocumentScorer(stopWords).score(doc)

        val topNodeHtml = node?.let { n -> htmlScoredCleaner.cleanHtml(n) }
        val articleElement = doc.selectFirst("article")
        val topNodeHtmlArticle = articleElement?.let { n -> htmlScoredCleaner.cleanHtml(n) }

        // lookup top nodes from original document
        val topNode = nodeMap[topNodeHtml?.attr("essenceNodeId")]
        val topNodeArticle = nodeMap[topNodeHtmlArticle?.attr("essenceNodeId")]

        val favorite = listOf(topNode, topNodeArticle).maxBy { n -> n.toString().length }

        val parts = favorite?.let { fav -> htmlFormatter.formatElement(fav) } ?: listOf()
        return parts
    }

    /**
     * appends a unique id to each source element in the document and returns a map of uuid to element.
     */
    @OptIn(ExperimentalUuidApi::class)
    private fun createNodeIds(element: Element, nodeMap: MutableMap<String, Element> = mutableMapOf()): Map<String, Element> {
        val essenceNodeId = Uuid.random().toString()
        nodeMap[essenceNodeId] = element
        element.attr("essenceNodeId", essenceNodeId)
        element.childElementsList().forEach { child -> createNodeIds(child, nodeMap) }
        return nodeMap
    }
}
