package org.jlleitschuh.security.checklist.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.required
import org.jlleitschuh.security.checklist.core.Checklist
import org.jlleitschuh.security.checklist.parser.NamedChecklist
import org.jlleitschuh.security.checklist.parser.loadChecklist
import org.jlleitschuh.security.checklist.uploader.github.GitHubUploadInfo
import org.jlleitschuh.security.checklist.uploader.github.GitHubUploader

class ChecklistTransformer : CliktCommand(invokeWithoutSubcommand = true) {
    private val listNames by option()
    private val capitalizePhase: Boolean by option().flag(default = true)
    private val prependListNameToPhase: Boolean by option().flag(default = true)
    private val checklists by findObject { mutableListOf<Checklist>() }

    override fun run() {
        val earlyChecklist =
            NamedChecklist
                .values()
                .map { loadChecklist(it) }
                .mapIf(capitalizePhase) { doCapitalizePhase(it) }
                .mapIf(prependListNameToPhase) { doPrependListNameToPhase(it)}

        checklists.addAll(earlyChecklist)

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
}

class GitHub : CliktCommand() {

    private val repoOwner by option().prompt()
    private val repoName by option().prompt()
    private val projectName by option().prompt(default = "Security Roadmap")
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
