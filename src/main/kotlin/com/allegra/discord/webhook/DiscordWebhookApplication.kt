package com.allegra.discord.webhook

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DiscordWebhookApplication

fun main(args: Array<String>) {
	runApplication<DiscordWebhookApplication>(*args)
}
