package com.allegra.discord.webhook.model


data class DiscordModel(
    val embeds: List<Embed>
) {
    constructor(jellyfinModel: JellyfinModel) :
            this(listOf(Embed(jellyfinModel)))
}

data class Embed(
    val author: Author,
    val description: String,
    val thumbnail: Thumbnail,
    val footer: Footer,
    val color: Int,
    val timestamp: String,
) {
    constructor(jellyfinModel: JellyfinModel) :
            this(
                Author(jellyfinModel.name, jellyfinModel.url),
                jellyfinModel.description,
                Thumbnail("attachment://thumbnail.jpg"),
                Footer(jellyfinModel.footer),
                jellyfinModel.color,
                jellyfinModel.timestamp
            )
}

data class Author(
    val name: String,
    val url: String,
)

data class Thumbnail(
    val url: String
)

data class Footer(
    val text: String
)
