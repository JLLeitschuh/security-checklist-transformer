package org.jlleitschuh.security.checklist.app

import org.jlleitschuh.security.checklist.core.Checklist
import org.jlleitschuh.security.checklist.core.ChecklistGroup
import org.jlleitschuh.security.checklist.core.ChecklistItem

fun doCapitalizePhase(checklist: Checklist): Checklist {
    return checklist.transformItems { item ->
        item.copy(phase = item.phase.splitToSequence(" ").map { it.capitalize() }.joinToString(separator = " "))
    }
}

fun doPrependListNameToPhase(checklist: Checklist): Checklist {
    return checklist.transformItems {item ->
        item.copy(phase = "${checklist.name}: ${item.phase}")
    }
}

private fun Checklist.transformItems(itemTransformer: (ChecklistItem) -> ChecklistItem): Checklist {
    return transformGroups { group ->
        group.copy(items = group.items.map(itemTransformer))
    }
}

private fun Checklist.transformGroups(groupTransformer: (ChecklistGroup) -> ChecklistGroup): Checklist {
    return copy(items = items.map { groupTransformer(it) })
}
