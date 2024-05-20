package com.allegra.discord.webhook.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JsonIgnoreProperties(ignoreUnknown = true)
data class JellyfinModel(
    val name: String = "",
    val url: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val footer: String = "",
    val color: Int = 3381759,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
)
