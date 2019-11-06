package org.jlleitschuh.security.checklist.uploader.github

import org.jlleitschuh.security.checklist.core.Checklist
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
        repository.createIssuesFor(checklists) { columns.getValue(it) }
    }

    private fun GHProject.addColumns(checkLists: Collection<Checklist>): Map<String, GHProjectColumn> {
        val phases =
            checkLists.flatMap { list -> list.phases.flatMap { list.phases } }.toSet()
        val columns =
            listColumns().iterator().asSequence().map { it.name }.toSet()
        val toCreate = phases - columns
        println("To create the following columns: ${toCreate.joinToString()}")
        // First create the columns
        toCreate.forEach { createColumn(it) }
        // Then get all of the columns back again
        return listColumns().iterator().asSequence().map { it.name to it }.toMap()
    }

    private fun GHRepository.addLabels(checkList: Collection<Checklist>) {
        val categories =
            checkList.flatMap { list -> list.items }.map { it.name }.toSet()
        val labels =
            listLabels().iterator().asSequence().map { it.name }.toSet()
        val toCreate = categories - labels
        println("To create the following categories: ${toCreate.joinToString()}")
        toCreate.forEach { createLabel(it, labelColor) }
    }

    private fun GHRepository.createIssuesFor(checkLists: Collection<Checklist>, columnProvider: (name: String) -> GHProjectColumn) {
        checkLists.forEach { createIssuesFor(it, columnProvider) }
    }

    private fun GHRepository.createIssuesFor(checklist: Checklist, columnProvider: (name: String) -> GHProjectColumn) {
        checklist.items.forEach { group ->
            group.items.forEach { item ->
                val issue =
                    createIssue(item.name)
                        .body(item.body)
                        .label(group.name)
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
