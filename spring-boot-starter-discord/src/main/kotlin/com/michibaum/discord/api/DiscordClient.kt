package com.michibaum.discord.api

import com.michibaum.discord.api.dtos.*
import reactor.core.publisher.Mono

interface DiscordClient {
    /**
     * Sends a message to the specified Discord channel.
     *
     * @param channelId The unique identifier of the Discord channel where the message will be sent.
     * @param messageDto The data transfer object containing the content of the message to be sent.
     * @return A Mono emitting the DTO of the sent message, containing details such as its unique identifier and content.
     */
    fun sendMessage(channelId: String, messageDto: CreateMessageDto): Mono<GetMessageDto>

    /**
     * Retrieves information about the guild associated with the Discord client.
     *
     * @return A Mono emitting the DTO containing details of the guild, such as ID, name, description, features, owner ID, application ID, verification level, roles, default message
     *  notifications, max members, max video channel users, and preferred locale.
     */
    fun getGuild(): Mono<GetGuildeDto>

    /**
     * Retrieves a list of channels for the associated Discord guild.
     *
     * @return A Mono emitting a list of GetChannelDto objects, each representing details of a Discord channel.
     */
    fun getChannels(): Mono<List<GetChannelDto>>

    /**
     * Creates a new Discord channel based on the provided channel details.
     *
     * @param channelDto The data transfer object containing the details required to create the new channel.
     * @return A Mono emitting the DTO of the created channel, containing its unique identifier, name, type, guild ID, and other relevant details.
     */
    fun createChannel(channelDto: CreateChannelDto): Mono<GetChannelDto>
}