package io.github.cdimascio.essence.cleaners

import io.github.cdimascio.essence.cleaners.rules.Rule
import io.github.cdimascio.essence.util.find
import io.github.cdimascio.essence.util.matchFirstElementTags
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeFilter

private const val GRAVITY_USED_ALREADY = "grv-usedalready"

class Cleaner() {

    companion object {

        val tags = listOf("a", "blockquote", "dl", "div", "img", "ol", "p", "pre", "table", "ul")

        val tagConversionExceptions = listOf("h1", "h2", "h3", "h4")
        val tagsToConvert = listOf("div", "span")
    }

    fun clean(doc: Document): Document {
        removeBodyClasses(doc)
        cleanEmTags(doc)
        cleanCodeBlocks(doc)
        removeDropCaps(doc)
        removeScriptsStyles(doc)
        Traverse(
            nodeRemovalRules = listOf(
//                Rule::removeNonTextNodes,
                Rule::removeCommentsTravRule,
                Rule::removeBadTagsTravRule,
                Rule::removeNavigationElements,
                Rule::removeSponsoredContent,
                (Rule::removeMatching)("""^caption$""".toRegex()),
                (Rule::removeMatching)(""" google """.toRegex()),
                (Rule::removeMatching)("""^[^entry-]more.*$""".toRegex()),
                (Rule::removeMatching)("""(["#.'\-_]+|^)(fb|facebook)[\-_"']+|facebook-broadcasting""".toRegex()),
                (Rule::removeMatching)("""[^-]twitter""".toRegex())),
            nodeModificationRules = listOf(
                Rule::correctErrantLineBreaks,
                Rule::cleanArticleTag
            ))
            .applyRules(doc)
        cleanParaSpans(doc)
        cleanUnderlines(doc)
        elementToParagraph(doc)

        return doc
    }

    /**
     * Remove all classes from body
     */
    private fun removeBodyClasses(doc: Document) {
        val body = doc.body()
        body.classNames().forEach {
            body.removeClass(it)
        }
    }

    private fun cleanEmTags(doc: Document) {
        val ems = doc.getElementsByTag("em")
        ems.forEach {
            val images = it.find("img")
            if (images.isEmpty()) {
                it.unwrap()
            }
        }
    }

    private fun cleanCodeBlocks(doc: Document) {
        val nodes = doc.select(
            "[class*='highlight-'], pre code, code, pre, ul.task-list"
        )
        nodes.forEach {
            it.unwrap()
        }
    }

    private fun removeDropCaps(doc: Document) {
        val nodes = doc.select("span[class~=dropcap], span[class~=drop_cap]")
        return nodes.forEach {
            it.unwrap()
        }
    }

    private fun removeScriptsStyles(doc: Document) {
        doc.getElementsByTag("script").remove()
        doc.getElementsByTag("style").remove()
    }

    private fun cleanParaSpans(doc: Document) {
        doc.select("p span").forEach {
            it.unwrap()
        }
    }

    private fun cleanUnderlines(doc: Document) {
        doc.select("u").forEach {
            it.unwrap()
        }
    }

    private fun elementToParagraph(doc: Document) {
        doc.select(tagsToConvert.joinToString(","))
            .forEach { element ->
                if (element.matchFirstElementTags(tags, 1).isEmpty()) {
                    if (!tagConversionExceptions.contains(element.parent()?.tagName())) {
                        val html = element.html()
                        element.tagName("p")
                        element.html(html)
                    } else {
                        element.unwrap()
                    }
                } else {
                    val pReplacementElements = getReplacementNodes(element)
                        .map { e -> e.html() }
                        .filter { h -> h.isNotEmpty() }
                        .map { html -> Element("p").html(html) }
                    if (pReplacementElements.isNotEmpty()) {
                        element.parent()?.insertChildren(
                            element.siblingIndex(),
                            pReplacementElements
                        )
                        element.remove()
                    }
                }
            }
    }

    private fun getReplacementNodes(div: Node): List<Element> {
        val nodesToReturn = mutableListOf<Element>()
        val nodesToRemove = mutableListOf<Element>()
        val replacementText = mutableListOf<String>()

        val isGravityUsed = { e: Element -> e.attr(GRAVITY_USED_ALREADY) == "yes" }
        val setGravityUsed = { e: Element -> e.attr(GRAVITY_USED_ALREADY, "yes") }

        div.childNodes().forEach { kid ->
            if (kid is Element && kid.tagName() == "p" && replacementText.isNotEmpty()) {
                val html = replacementText.joinToString(" ")
                nodesToReturn.add(Element("p").html(html))
                replacementText.clear()
                nodesToReturn.add(kid)
            } else if (kid is TextNode) {
                val kidText = kid.text()
                    .replace("""\n""", "\n\n")
                    .replace("""\t""", "")
                    .replace("""^\s+$""", "")
                if (kidText.length > 1) {
                    var prevSibling = kid.previousSibling()
                    while (prevSibling is Element && prevSibling.tagName() == "a" && !isGravityUsed(
                            prevSibling
                        )) {
                        val outerHtml = " ${prevSibling.outerHtml()} "
                        replacementText.add(outerHtml)
                        nodesToRemove.add(prevSibling)
                        setGravityUsed(prevSibling)
                        prevSibling = prevSibling.previousSibling()
                    }

                    replacementText.add(kidText)

                    var nextSibling = kid.nextSibling()
                    while (nextSibling is Element && nextSibling.tagName() == "a" && !isGravityUsed(
                            nextSibling
                        )) {
                        val outerHtml = " ${nextSibling.outerHtml()} "
                        replacementText.add(outerHtml)
                        nodesToRemove.add(nextSibling)
                        setGravityUsed(nextSibling)
                        nextSibling = nextSibling.nextSibling()
                    }
                }
            } else {
                if (kid is Element) {
                    nodesToReturn + kid
                }
            }
        }

        if (replacementText.isNotEmpty()) {
            val html = replacementText.joinToString(" ")
            nodesToReturn.add(Element("p").html(html))
            replacementText.clear()
        }

        for (node in nodesToRemove) {
            node.remove()
        }

        val isInteresting = { e: Element ->
            !listOf("meta", "head").contains(e.tagName())
        }
        return nodesToReturn.filter { isInteresting(it) }
    }
}

class Traverse(
    private val nodeRemovalRules: List<(Node) -> Boolean>,
    private val nodeModificationRules: List<(Node) -> Unit>) {

    fun applyRules(node: Node): Traverse {
        node.filter(object : NodeFilter {
            override fun tail(node: Node, depth: Int): NodeFilter.FilterResult {
                return NodeFilter.FilterResult.CONTINUE
            }

            override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
                for (rule in nodeModificationRules) {
                    rule(node)
                }

                for (rule in nodeRemovalRules) {
                    if (rule(node)) {
                        return NodeFilter.FilterResult.REMOVE
                    }
                }
                return NodeFilter.FilterResult.CONTINUE
            }
        })
        return this
    }
}

