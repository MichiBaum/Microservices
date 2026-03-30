package com.michibaum.admin_service.config

import com.michibaum.discord.api.DiscordClient
import com.michibaum.discord.config.DiscordAutoConfiguration
import de.codecentric.boot.admin.server.config.AdminServerNotifierAutoConfiguration
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@AutoConfiguration
@AutoConfigureBefore(value = [AdminServerNotifierAutoConfiguration::class, AdminServerNotifierAutoConfiguration.CompositeNotifierConfiguration::class])
@AutoConfigureAfter(value = [DiscordAutoConfiguration::class])
@EnableConfigurationProperties(AdminDiscordProperties::class)
class NotifierConfiguration {

    @Bean
    @ConditionalOnBean(value = [DiscordClient::class])
    fun customDiscordNotifier(discordClient: DiscordClient, adminDiscordProperties: AdminDiscordProperties, instanceRepository: InstanceRepository): CustomDiscordNotifier =
        CustomDiscordNotifier(discordClient, adminDiscordProperties, instanceRepository)

}