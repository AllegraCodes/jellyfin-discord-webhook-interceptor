package com.allegra.discord.webhook.controller


import com.allegra.discord.webhook.model.DiscordModel
import com.allegra.discord.webhook.model.JellyfinModel
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

private val logger = KotlinLogging.logger {}

@RestController
class WebhookController {
    @Value("\${allegra.discord.webhook.url}")
    private lateinit var discordUrl: String

    @PostMapping("/webhook")
    fun webhook(@RequestBody body: JellyfinModel): ResponseEntity<String> {
        // received a new message from jellyfin webhooks plugin
        logger.info { "received a new message from jellyfin webhooks plugin" }
        logger.debug { body }

        val webClient = WebClient.create()

        // get the image referenced in the thumbnail
        logger.info { "getting thumbnail from jellyfin" }
        var image: ByteArray? = null
        try {
            image = webClient.get()
                .uri(body.thumbnail + "?format=Jpg&maxWidth=300")
                .retrieve()
                .toEntity(ByteArray::class.java)
                .block()
                ?.body
        } catch (e: Exception) {
            logger.error { "failed to get thumbnail from Jellyfin" }
        }

        // convert the JellyfinModel to a DiscordModel
        val model = DiscordModel(body)
        logger.debug { "discord model: $model" }

        // bundle the image file and model for the discord webhook
        logger.info { "preparing message for discord" }
        val discordBody = MultipartBodyBuilder().apply {
            part("payload_json", jacksonObjectMapper().writeValueAsString(model))
            part("files[0]", image ?: ByteArray(0))
                .header(HttpHeaders.CONTENT_DISPOSITION, "name=files[0];filename=thumbnail.jpg")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
        }.build()
        logger.debug { "discord body: $discordBody" }

        // send her off to discord!
        logger.info { "sending message to discord" }
        var status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        val response = webClient.post()
            .uri(discordUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"payload_json\"")
            .body(BodyInserters.fromMultipartData(discordBody))
            .exchangeToFlux {
                status = it.statusCode().value()
                it.bodyToFlux(String::class.java)
            }
            .blockLast()

        // pass back the response from discord
        if (status == HttpStatus.OK.value()) logger.info { "successfully sent message to discord" }
        else logger.error { "failed to send message to discord: returned status $status" }
        logger.debug { "status: $status" }
        logger.debug { "response: $response" }
        return ResponseEntity.status(status).body(response)
    }
}
