package com.michibaum.admin_service.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.boot.admin.notify.custom-discord")
data class AdminDiscordProperties(
    var enabled: Boolean = false,
    var channelId: String = "",
)