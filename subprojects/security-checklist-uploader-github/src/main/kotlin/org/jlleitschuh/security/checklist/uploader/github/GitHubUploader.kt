package org.jlleitschuh.security.checklist.uploader.github

import org.jlleitschuh.security.checklist.core.Checklist
import org.jlleitschuh.security.checklist.core.ChecklistItem
import org.kohsuke.github.GHIssueState
import org.kohsuke.github.GHMilestone
import org.kohsuke.github.GHProject
import org.kohsuke.github.GHProjectColumn
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import java.io.IOException

object GitHubUploader {
    private const val labelColor = "3d77f5"

    fun setup(ghInfo: GitHubUploadInfo, checklists: Collection<Checklist>) {
        val gitHub = GitHub.connect()

        val repository by lazy {
            gitHub.getOrCreateRepository(ghInfo)
        }

        val project =
            GitHubCaller(
                ghInfo
            ) { repository }
                .getOrCreateProject()

        val columns =
            project.addColumns(checklists)
        repository.addLabels(checklists)
        val milestones =
            repository.addMilestones(checklists)
        repository.createIssuesFor(
            checklists,
            columnProvider = { columns.getValue(it) },
            milestoneProvider = { milestones.getValue(it) }
        )
    }

    private fun GHProject.addColumns(checklists: Collection<Checklist>): Map<String, GHProjectColumn> {
        val phases =
            checklists.flatMap { list -> list.phases.flatMap { list.phases } }.toSet()
        val columns =
            listColumns().asSequence().map { it.name }.toSet()
        val toCreate = phases - columns
        println("To create the following columns: ${toCreate.joinToString()}")
        // First create the columns
        toCreate.forEach { createColumn(it) }
        // Then get all of the columns back again
        return listColumns().asSequence().map { it.name to it }.toMap()
    }

    private fun GHRepository.addLabels(checklists: Collection<Checklist>) {
        val categories =
            checklists.flatMap { list -> list.items }.map { group -> group.name }.toSet()
        val labels =
            listLabels().asSequence().map { it.name }.toSet()
        val toCreate = categories - labels
        println("To create the following categories: ${toCreate.joinToString()}")
        toCreate.forEach { createLabel(it, labelColor) }
    }

    private fun GHRepository.addMilestones(checklists: Collection<Checklist>): Map<String, GHMilestone> {
        // Check for existing milestones
        val milestones =
            listMilestones(GHIssueState.OPEN).asSequence().map { it.title }.toSet()
        // Create the milestones
        checklists.forEach { checklist ->
            val milestoneBody = "[${checklist.name} Checklist](${checklist.url})"
            checklist
                .items
                .flatMap { group -> group.items }
                .map { item -> checklist.milestoneName(item) }
                .distinct()
                .filter { milestoneName -> !milestones.contains(milestoneName) } // Don't create a new milestone if one already exists
                .forEach { milestoneName -> createMilestone(milestoneName, milestoneBody) }
        }
        // Grab all existing milestones
        return listMilestones(GHIssueState.OPEN).asSequence().map { it.title to it }.toMap()
    }

    private fun GHRepository.createIssuesFor(
        checklists: Collection<Checklist>,
        columnProvider: (name: String) -> GHProjectColumn,
        milestoneProvider: (name: String) -> GHMilestone
    ) {
        checklists.forEach { createIssuesFor(it, columnProvider, milestoneProvider) }
    }

    private fun GHRepository.createIssuesFor(
        checklist: Checklist,
        columnProvider: (name: String) -> GHProjectColumn,
        milestoneProvider: (name: String) -> GHMilestone
    ) {
        println("Uploading Checklist: ${checklist.name}")
        val size = checklist.items.flatMap { it.items }.count()
        var index = 1
        checklist.items.forEach { group ->
            group.items.forEach { item ->
                println("\t[$index/$size] ${item.name}")
                index++
                val milestone =
                    milestoneProvider(checklist.milestoneName(item))
                val issue =
                    createIssue(item.name)
                        .body(item.body)
                        .label(group.name)
                        .milestone(milestone)
                        .create()
                columnProvider(item.phase).createCard(issue)
            }
        }
    }

    private fun GitHub.getOrCreateRepository(ghInfo: GitHubUploadInfo): GHRepository =
        try {
            getRepository(ghInfo.repositoryNamedIdentifier)
        } catch (e: IOException) {
            if (myself.login.equals(ghInfo.repositoryOwner, true)) {
                createRepository(ghInfo.repositoryName).create()
            } else {
                getOrganization(ghInfo.repositoryOwner).createRepository(ghInfo.repositoryName).create()
            }
        }

    private fun Checklist.milestoneName(item: ChecklistItem) =
        "$name Checklist: ${item.phase}"
}


private class GitHubCaller(
    private val ghInfo: GitHubUploadInfo,
    private val repositoryCreator: () -> GHRepository
) {
    private val repository by lazy {
        repositoryCreator()
    }

    internal fun getOrCreateProject() =
        repository.listProjects().asSequence().singleOrNull { it.name.equals(ghInfo.projectName, true) }
            ?: repository.createProject(ghInfo.projectName, null)
}
