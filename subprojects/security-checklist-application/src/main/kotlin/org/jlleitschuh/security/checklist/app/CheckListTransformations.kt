package org.jlleitschuh.security.checklist.app

import org.jlleitschuh.security.checklist.core.Checklist
import org.jlleitschuh.security.checklist.core.ChecklistGroup
import org.jlleitschuh.security.checklist.core.ChecklistItem

fun doCapitalizePhase(checklist: Checklist): Checklist {
    return checklist.transformItems { item ->
        item.copy(phase = item.phase.capitalizeAll())
    }
}

fun doCapitalizeGroup(checklist: Checklist): Checklist {
    return checklist.transformGroups { group ->
        group.copy(name = group.name.capitalizeAll())
    }
}

fun doPrependListNameToPhase(checklist: Checklist): Checklist {
    return checklist.transformItems { item ->
        item.copy(phase = "${checklist.shortName}: ${item.phase}")
    }
}

fun doSemanticFixes(checklist: Checklist): Checklist {
    return checklist.transformGroups { group ->
        if (group.name.startsWith("your ", true)) {
            group.copy(name = group.name.removeRange(0, 5))
        } else {
            group
        }
    }
}

fun doQuoteBody(checklist: Checklist): Checklist {
    return checklist.transformItems { item ->
        val splitBody = item.body.split('\n').toMutableList()
        // Add the citation quote
        splitBody.add("") // Extra newline
        splitBody.add(" \\- [${checklist.name} Checklist](${checklist.url})")
        val bodyQuoted =
            splitBody.joinToString(separator = "\n") { "> $it" }
        item.copy(body = bodyQuoted)
    }
}

private fun String.capitalizeAll() =
    splitToSequence(" ").map { it.capitalize() }.joinToString(separator = " ")

private fun Checklist.transformItems(itemTransformer: (ChecklistItem) -> ChecklistItem): Checklist {
    return transformGroups { group ->
        group.copy(items = group.items.map(itemTransformer))
    }
}

private fun Checklist.transformGroups(groupTransformer: (ChecklistGroup) -> ChecklistGroup): Checklist {
    return copy(items = items.map { groupTransformer(it) })
}
