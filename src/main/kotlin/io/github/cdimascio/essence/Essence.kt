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
import io.github.cdimascio.essence.extractors.TagsExtractor
import io.github.cdimascio.essence.extractors.TextExtractor
import io.github.cdimascio.essence.extractors.TitleExtractor
import io.github.cdimascio.essence.extractors.VideosExtractor
import io.github.cdimascio.essence.model.EssenceResult
import io.github.cdimascio.essence.model.Language
import io.github.cdimascio.essence.scorers.DocumentScorer
import io.github.cdimascio.essence.words.StopWords
import org.jsoup.Jsoup

object Essence {

    @JvmStatic
    fun extract(html: String, lang: Language? = null): EssenceResult {
        val document = Jsoup.parse(html)

        // clean and score document before extracting text, links and video
        Cleaner.clean(document)
        val stopWords = StopWords.load(
            lang ?: Language.from(LanguageExtractor.extract(document))
        )
        val node = DocumentScorer.score(document, stopWords)
        ScoreCleaner.clean(node, stopWords)

        return EssenceResult(
            authors = AuthorExtractor.extract(document),
            title = TitleExtractor.extract(document),
            softTitle = TitleExtractor.extract(document, soft = true),
            description = DescriptionExtractor.extract(document),
            publisher = PublisherExtractor.extract(document),
            date = DataExtractor.extract(document),
            copyright = CopyrightExtractor.extract(document),
            language = (lang ?: Language.from(LanguageExtractor.extract(document))).name,
            text = TextExtractor.extract(node, stopWords),
            videos = VideosExtractor.extract(node),
            favicon = FaviconExtractor.extract(document),
            image = ImageExtractor.extract(document),
            links = LinksExtractor.extract(node),
            canonicalLink = CanonicalExtractor.extract(document),
            keywords = KeywordsExtractor.extract(document),
            tags = TagsExtractor.extract(document)
        )
    }
}
