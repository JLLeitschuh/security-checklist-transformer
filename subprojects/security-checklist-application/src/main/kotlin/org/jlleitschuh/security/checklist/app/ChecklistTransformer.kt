package org.jlleitschuh.security.checklist.app

import com.github.ajalt.clikt.core.CliktCommand
import org.jlleitschuh.security.checklist.parser.NamedChecklist
import org.jlleitschuh.security.checklist.parser.loadChecklist

class ChecklistTransformer : CliktCommand() {
    override fun run() {
        val checklists = NamedChecklist.values().map { loadChecklist(it) }
        checklists.forEach { list ->
            println(list.toIndentedString(0))
            println()
        }
        checklists.forEach { list ->
            println(list.countOverview())
        }
    }
}

fun main(args: Array<String>) {
    ChecklistTransformer().main(args)
}
