package com.michibaum.admin_service.notifier

import com.michibaum.discord.api.DiscordClient
import de.codecentric.boot.admin.server.config.AdminServerNotifierAutoConfiguration
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository
import de.codecentric.boot.admin.server.notify.Notifier
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@AutoConfigureBefore(value = [AdminServerNotifierAutoConfiguration::class, AdminServerNotifierAutoConfiguration.CompositeNotifierConfiguration::class])
class NotifierConfiguration {

    @Bean
    fun adminDiscordProperties(): AdminDiscordProperties =
        AdminDiscordProperties()

    // TODO doesnt work yet
    @Bean
    @ConditionalOnBean(value = [DiscordClient::class])
    fun customDiscordNotifier(discordClient: DiscordClient, adminDiscordProperties: AdminDiscordProperties, instanceRepository: InstanceRepository): Notifier =
        CustomDiscordNotifier(discordClient, adminDiscordProperties, instanceRepository)

}