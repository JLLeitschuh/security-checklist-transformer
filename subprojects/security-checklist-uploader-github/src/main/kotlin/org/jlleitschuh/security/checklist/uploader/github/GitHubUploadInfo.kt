package org.jlleitschuh.security.checklist.uploader.github

data class GitHubUploadInfo(
    val repositoryOwner: String,
    val repositoryName: String,
    val projectName: String
) {
    val repositoryNamedIdentifier by lazy {
        "$repositoryOwner/$repositoryName"
    }
}
