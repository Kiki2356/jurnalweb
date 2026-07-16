package com.jurnal.data

data class Journal(
    val title: String = "",
    val date: String = "",
    val tags: List<String> = emptyList(),
    val mood: String = "",
    val music: String = "",
    val albumArt: String = "",
    val image: String = "",
    val content: String = ""
)

data class GitHubContent(
    val name: String = "",
    val content: String = ""
)

data class GitHubCommit(
    val message: String = "",
    val content: String = "",
    val branch: String = "main"
)
