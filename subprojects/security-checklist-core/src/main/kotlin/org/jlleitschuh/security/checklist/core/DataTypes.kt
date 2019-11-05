package org.jlleitschuh.security.checklist.core


data class Checklist(
    val name: String,
    val items: List<ChecklistGroup>
) {
    fun toIndentedString(indents: Int): String {
        val indent = buildIndentSize(indents)
        return buildString {
            append(indent)
            append(name)
            append('\n')
            append(items.joinToString(separator = "\n") { it.toIndentedString(indents + 1) })
        }
    }

    fun countOverview() = "$name: ${items.map { it.items.count() }.sum()}"
}

data class ChecklistGroup(
    val name: String,
    val items: List<ChecklistItem>
) {
    fun toIndentedString(indents: Int): String {
        val indent = buildIndentSize(indents)
        return buildString {
            append(indent)
            append(name)
            append('\n')
            append(items.joinToString(separator = "\n") { it.toIndentedString(indents + 1) })
        }
    }
}

data class ChecklistItem(
    val name: String,
    val phase: String,
    val body: String
) {
    fun toIndentedString(indents: Int): String {
        val indent = buildIndentSize(indents)
        return buildString {
            append(indent)
            append(name)
            append(" ")
            append("[$phase]")
            append('\n')
            body.split('\n').forEach { line ->
                append(indent)
                append('\t')
                append(line)
                append('\n')
            }
        }
    }
}

private fun buildIndentSize(indents: Int) = buildString { repeat(indents) { append('\t') } }
