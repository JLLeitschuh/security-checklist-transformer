package org.jlleitschuh.security.checklist.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.required
import org.jlleitschuh.security.checklist.core.Checklist
import org.jlleitschuh.security.checklist.parser.NamedChecklist
import org.jlleitschuh.security.checklist.parser.loadChecklist
import org.jlleitschuh.security.checklist.uploader.github.GitHubUploadInfo
import org.jlleitschuh.security.checklist.uploader.github.GitHubUploader

class ChecklistTransformer : CliktCommand(invokeWithoutSubcommand = true) {
    private val listNames by option(help = "The names of the lists to use. Default: $defaultListNamesFormatted")
        .multiple(default = defaultListNames)
    private val capitalizePhase: Boolean by option(help = "Capitalize the phase names. $defaultTrueMessage").flag(default = true)
    private val capitalizeGroup: Boolean by option(help = "Capitalize the group names. $defaultTrueMessage").flag(default = true)
    private val prependListNameToPhase: Boolean by option(help = "Prepend the list name to the phase. $defaultTrueMessage").flag(default = true)
    private val variousSemanticFixes: Boolean by option(help = "Apply other semantic fixes to the data. $defaultTrueMessage").flag(default = true)
    private val quoteBodyText: Boolean by option(help = "Quote body text and cite source in issue body. $defaultTrueMessage").flag(default = true)

    // Used to transfer configuration between commands
    private val checklists by findObject { mutableListOf<Checklist>() }

    private fun loadChecklist(): List<Checklist> {
        val requestedChecklists =
            NamedChecklist
                .values()
                .filter { listNames.contains(it.name) }

        return requestedChecklists
            .map { loadChecklist(it) }
            .mapIf(capitalizePhase) { doCapitalizePhase(it) }
            .mapIf(capitalizeGroup) { doCapitalizeGroup(it) }
            .mapIf(prependListNameToPhase) { doPrependListNameToPhase(it) }
            .mapIf(variousSemanticFixes) { doSemanticFixes(it) }
            .mapIf(quoteBodyText) { doQuoteBody(it) }
    }

    override fun run() {
        checklists.addAll(loadChecklist())

        if (context.invokedSubcommand == null) {
            checklists.forEach { list ->
                println(list.toIndentedString(0))
                println()
            }
            checklists.forEach { list ->
                println(list.countOverview())
            }
        }
    }

    companion object {
        private val defaultListNames =
            NamedChecklist.values().map { it.name }
        private val defaultListNamesFormatted =
            defaultListNames.joinToString()

        private const val defaultTrueMessage = "Default: true"
    }
}

class GitHub : CliktCommand(help = "Upload to GitHub") {

    private val repoOwner by option(help = "The owner of the repository").prompt()
    private val repoName by option(help = "The name of the repository").prompt()
    private val projectName by option(help = "The name of the project board").prompt(default = "Security Roadmap")
    private val checklists by requireObject<List<Checklist>>()

    override fun run() {
        val info = GitHubUploadInfo(
            repositoryOwner = repoOwner,
            repositoryName = repoName,
            projectName = projectName
        )
        GitHubUploader.setup(info, checklists)
    }
}

fun main(args: Array<String>) {
    ChecklistTransformer().subcommands(GitHub()).main(args)
}
