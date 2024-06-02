package io.github.cdimascio.essence.cleaners

import io.github.cdimascio.essence.cleaners.rules.Rule
import io.github.cdimascio.essence.util.find
import io.github.cdimascio.essence.util.matchFirstElementTags
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeFilter
import java.lang.StringBuilder


object Cleaner {

    private const val GRAVITY_USED_ALREADY = "grv-usedalready"

    private val tags = listOf("a", "blockquote", "dl", "div", "img", "ol", "p", "pre", "table", "ul")


    fun clean(document: Document) {
        removeBodyClasses(document)
        cleanEmTags(document)
        cleanCodeBlocks(document)
        removeDropCaps(document)
        removeScriptsStyles(document)
        applyRules(
            node = document,
            nodeRemovalRules = listOf(
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
            )
        )
        cleanParaSpans(document)
        cleanUnderlines(document)
        elementToParagraph(document, listOf("div", "span"))
    }

    private fun applyRules(
        node: Node,
        nodeRemovalRules: List<(Node) -> Boolean>,
        nodeModificationRules: List<(Node) -> Unit>
    ) {
        node.filter(object : NodeFilter {
            override fun tail(node: Node, depth: Int): NodeFilter.FilterResult {
                return NodeFilter.FilterResult.CONTINUE
            }

            override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
                nodeModificationRules.forEach { it(node) }
                nodeRemovalRules.forEach { if (it(node)) return NodeFilter.FilterResult.REMOVE }
                return NodeFilter.FilterResult.CONTINUE
            }
        })
    }

    /**
     * Remove all classes from body
     */
    private fun removeBodyClasses(document: Document) {
        document.body().classNames().forEach {
            document.body().removeClass(it)
        }
    }

    private fun cleanEmTags(document: Document) {
        document
            .getElementsByTag("em")
            .forEach {
                val images = it.find("img")
                if (images.isEmpty()) {
                    it.unwrap()
                }
            }
    }

    private fun cleanCodeBlocks(document: Document) {
        document.select(
            "[class*='highlight-'], pre code, code, pre, ul.task-list"
        ).forEach { it.unwrap() }
    }

    private fun removeDropCaps(document: Document) {
        return document.select("span[class~=dropcap], span[class~=drop_cap]")
            .forEach { it.unwrap() }
    }

    private fun removeScriptsStyles(document: Document) {
        document.getElementsByTag("script").remove()
        document.getElementsByTag("style").remove()
    }

    private fun cleanParaSpans(document: Document) {
        document.select("p span").forEach { it.unwrap() }
    }

    private fun cleanUnderlines(document: Document) {
        document.select("u").forEach { it.unwrap() }
    }

    private fun elementToParagraph(document: Document, tagNames: List<String>) {
        document.select(tagNames.joinToString(",")).forEach { element ->
            val items = element.matchFirstElementTags(tags, 1)
            if (items.isEmpty()) {
                val html = element.html()
                element.tagName("p")
                element.html(html)

            } else {
                val replaceNodes = getReplacementNodes(element)
                val pReplacementElements = mutableListOf<Element>()
                replaceNodes.forEach { rNode ->
                    if (rNode.html().isNotEmpty()) {
                        pReplacementElements.add(Element("p").html(rNode.html()))
                    }
                }
                element.parent().insertChildren(element.siblingIndex(), pReplacementElements)
                element.remove()
            }
        }
    }

    private fun getReplacementNodes(div: Node): List<Element> {
        val nodesToRemove = mutableListOf<Node>()
        val replacmentText = StringBuilder()

        val nodesToReturn = mutableListOf<Element>()
        div.childNodes().forEach { child ->
            if (child is Element && child.tagName() == "p" && replacmentText.isNotEmpty()) {
                val html = replacmentText.toString()
                nodesToReturn.add(Element("p").html(html))
                replacmentText.clear()
                nodesToReturn.add(child)
            } else if (child is TextNode) {
                val childText = child.text()
                    .replace("""\n""", "\n\n")
                    .replace("""\t""", "")
                    .replace("""^\s+$""", "")
                if (childText.length > 1) {
                    processSibling(
                        sibling = child.previousSibling(),
                        replacmentText = replacmentText,
                        nodesToRemove = nodesToRemove
                    )

                    replacmentText.append(childText)

                    processSibling(
                        sibling = child.nextSibling(),
                        replacmentText = replacmentText,
                        nodesToRemove = nodesToRemove
                    )
                }
            } else {
                if (child is Element) {
                    nodesToReturn += child
                }
            }
        }

        if (replacmentText.isNotEmpty()) {
            val html = replacmentText.toString()
            nodesToReturn.add(Element("p").html(html))
            replacmentText.clear()
        }

        nodesToRemove.forEach { it.remove() }

        val isInteresting = { e: Element ->
            !listOf("meta", "head").contains(e.tagName())
        }

        return nodesToReturn.filter { isInteresting(it) }
    }

    private fun processSibling(
        sibling: Node?,
        replacmentText: StringBuilder,
        nodesToRemove: MutableList<Node>
    ) {
        var prevSibling1 = sibling
        while (prevSibling1 is Element && prevSibling1.tagName() == "a" && prevSibling1.attr(
                GRAVITY_USED_ALREADY
            ) != "yes"
        ) {
            val outerHtml = " ${prevSibling1.outerHtml()} "
            replacmentText.append(outerHtml)
            nodesToRemove.add(prevSibling1)
            prevSibling1.attr(GRAVITY_USED_ALREADY, "yes")
            prevSibling1 = prevSibling1.previousSibling()
        }
    }
}
