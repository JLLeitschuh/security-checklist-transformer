package org.jlleitschuh.security.checklist.parser


import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.lang.RuntimeException

/**
 * Simple HTML to Markdown converter.
 * Not intended to to be fully featured or completely correct.
 *
 * Loosely based upon the parser found here:
 * https://github.com/pnikosis/jHTML2Md/tree/master/src/main/java/com/pnikosis/html2markdown
 */
internal fun htmlToMarkdown(element: Element): String {
    return Parser().getTextContent(element)
}

private class MDLine (
    content: String = "",
    val level: Int = 0,
    val lineType: MDLineType = MDLineType.None
){
    private val theContent = StringBuilder(content)

    val content: String
        get() = theContent.toString()

    fun isEmpty() = content.isEmpty()

    fun append(toAppend: String) {
        if (isEmpty()) {
            var i = 0
            while (i < toAppend.length && Character.isWhitespace(toAppend[i])) {
                i++
            }
            theContent.append(toAppend.substring(i))
        } else {
            theContent.append(toAppend)
        }
    }

    override fun toString(): String {
        val newLine = StringBuilder()
        repeat(level) {
            newLine.append("    ")
        }

        @Suppress("NON_EXHAUSTIVE_WHEN")
        when(lineType) {
            MDLineType.Ordered -> newLine.append("1").append(". ")
            MDLineType.Unordered -> newLine.append("* ")
        }
        var content = theContent.toString()
        if (lineType == MDLineType.Unordered) {
            content = content.replace("^\n".toRegex(), "")
        }
        newLine.append(content)
        return newLine.toString()
    }

    companion object {
        fun empty() = MDLine()
    }
}

private enum class MDLineType {
    Ordered,
    Unordered,
    None
}

private typealias Lines = MutableList<MDLine>

private val Lines.theLast: MDLine
    get() {
        return if (isEmpty()) {
            val new = MDLine.empty()
            add(new)
            new
        } else {
            last()
        }
    }

private fun Lines.addEmpty() = add(MDLine.empty())

private class Parser {
    private var indentation: Int = 0
    private var orderedList: Boolean = false

    fun getTextContent(element: Element): String {
        val lines = mutableListOf<MDLine>()
        element.childNodes().forEach { child ->
            when (child) {
                is TextNode -> {
                    val line = lines.theLast
                    if (line.isEmpty()) {
                        if (!child.isBlank) {
                            line.append(child.text().replace("#".toRegex(), "/#").replace("\\*".toRegex(), "/\\*"))
                        }
                    } else {
                        line.append(child.text().replace("#".toRegex(), "/#").replace("\\*".toRegex(), "/\\*"))
                    }
                }
                is Element -> {
                    processElement(child, lines)
                }
                else -> throw RuntimeException("Unknown Element type: $element")
            }
        }

        var blankLines = 0
        val builder = StringBuilder()
        lines.forEachIndexed { i, mdLine ->
            val line = mdLine.toString().trim()
            if (line.isEmpty()) {
                blankLines ++
            } else {
                blankLines = 0
            }
            if (blankLines < 2) {
                builder.append(line)
                if (i < lines.size - 1) {
                    builder.append("\n")
                }
            }
        }
        return builder.toString()
    }

    private fun processElement(element: Element, lines: Lines) {
        val tag = element.tag()
        val tagName = tag.name
        when {
            tagName == "div" -> div(element, lines)
            tagName == "p" -> p(element, lines)
            tagName == "strong" || tagName == "b" -> strong(element, lines)
            tagName == "a" -> a(element, lines)
            tagName == "ul" -> ul(element, lines)
            tagName == "li" -> li(element, lines)
            else -> lines.theLast.append(getTextContent(element))
        }
    }

    private fun div(element: Element, lines: Lines) {
        val line = lines.theLast
        val content = getTextContent(element)

        if (content.isNotEmpty()) {
            if (line.content.trim().isNotEmpty()) {
                lines.addEmpty()
                lines.add(MDLine(content = content))
                lines.addEmpty()
            } else {
                if (content.trim().isNotEmpty()) {
                    line.append(content)
                }
            }
        }
    }

    private fun p(element: Element, lines: Lines) {
        val line = lines.theLast
        if (line.content.trim().isNotEmpty()) {
            lines.addEmpty()
        }
        lines.addEmpty()
        lines.add(MDLine(content = getTextContent(element)))
        lines.addEmpty()
        if (line.content.trim().isNotEmpty()) {
            lines.addEmpty()
        }
    }

    private fun strong(element: Element, lines: Lines) {
        val line = lines.theLast
        line.append("**")
        line.append(getTextContent(element))
        line.append("**")
    }

    private fun a(element: Element, lines: Lines) {
        val line = lines.theLast
        line.append("[")
        line.append(getTextContent(element))
        line.append("]")
        line.append("(")
        val url = element.attr("href")
        line.append(url)
        val title = element.attr("title")
        if(title.isNotEmpty()) {
            line.append(" \"")
            line.append(title)
            line.append("\"")
        }
        line.append(")")
    }

    private fun ul(element: Element, lines: Lines) {
        lines.addEmpty()
        indentation ++
        orderedList = false
        val line = MDLine.empty()
        line.append(getTextContent(element))
        lines.add(line)
        indentation --
        lines.addEmpty()
    }

    private fun li(element: Element, lines: Lines) {
        val lineType = if (orderedList) MDLineType.Ordered else MDLineType.Unordered
        lines.add(MDLine(level = indentation, lineType = lineType, content = getTextContent(element)))
    }
}
