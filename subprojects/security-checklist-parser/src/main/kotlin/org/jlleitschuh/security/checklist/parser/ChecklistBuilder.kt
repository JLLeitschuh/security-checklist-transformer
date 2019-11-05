package org.jlleitschuh.security.checklist.parser

import org.jlleitschuh.security.checklist.core.Checklist
import org.jlleitschuh.security.checklist.core.ChecklistGroup
import org.jlleitschuh.security.checklist.core.ChecklistItem
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.lang.RuntimeException
import java.nio.charset.Charset

enum class NamedChecklist
constructor(
    internal val descriptiveName: String,
    private val fileName: String,
    private val baseUri: String
) {
    SaaS_CTO(
        "SaaS CTO Checklist",
        "/saas-cto-security-checklist.html",
        "https://www.sqreen.com/checklists/saas-cto-security-checklist"
    ),
    SECURITY_ENGINEER(
        "Security Engineer Checklist",
        "/security-engineer-checklist.html",
        "https://www.sqreen.com/checklists/security-engineer-checklist"
    );

    internal fun loadDocument(): Document {
        val stream = NamedChecklist::class.java.getResourceAsStream(fileName)
        return stream.use {
            Jsoup.parse(stream, Charset.defaultCharset().toString(), baseUri)
        }
    }
}

fun loadChecklist(checklistName: NamedChecklist): Checklist {
    val document = checklistName.loadDocument()
    val groups = process(checklistName.loadDocument())
    return Checklist(
        checklistName.descriptiveName,
        groups
    )
}

private fun process(document: Document): List<ChecklistGroup> {
    val checkListContent = document.selectFirst(".checklist-content")
    val groups = checkListContent.select(".scrollspy")

    return groups.map { group ->
        val groupName = group.select("h2").text()
        val checkItems = group.select("li.single-item")
        val checklistItems = checkItems.map { checkItem ->
            val name = checkItem.selectFirst(".expend-bar>p").text()
            val category = checkItem.selectFirst(".expend-bar>span").text()
            val body = checkItem.selectFirst(".body-item")
            val markdownBody = bodyToMarkdown(body)
            ChecklistItem(
                name = name,
                phase = category,
                body = markdownBody
            )
        }
        ChecklistGroup(
            name = groupName,
            items = checklistItems
        )
    }
}

fun bodyToMarkdown(element: Element): String {
    if (element.hasLooseText()) {
        throw RuntimeException("div element contains raw text: `${element.cssSelector()}`")
    }
    return htmlToMarkdown(element)
}

/**
 * Check if an element contains text not already contained inside of another element.
 */
private fun Element.hasLooseText(): Boolean {
    for (child in childNodes()) {
        if (child is TextNode && !child.isBlank) {
            return true
        }
    }
    return false
}
