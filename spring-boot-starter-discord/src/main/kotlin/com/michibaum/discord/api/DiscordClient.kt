package com.michibaum.discord.api

import com.michibaum.discord.api.dtos.*

interface DiscordClient {
    /**
     * Sends a message to the specified Discord channel.
     *
     * @param channelId The unique identifier of the Discord channel where the message will be sent.
     * @param messageDto A data transfer object containing the content of the message to be sent.
     * @return A `GetMessageDto` object containing details of the message that was sent, including its unique ID, content, and the channel ID where it was sent.
     */
    fun sendMessage(channelId: String, messageDto: CreateMessageDto): GetMessageDto

    /**
     * Retrieves information about the guild associated with the Discord client.
     *
     * @return A `GetGuildeDto` object containing details such as the guild's ID, name, description, features, owner ID,
     * roles, and other attributes.
     */
    fun getGuild(): GetGuildeDto

    /**
     * Retrieves a list of channels associated with the configured Discord guild.
     *
     * This method makes an HTTP request to the Discord API to fetch all channels within the guild
     * using the client's guild ID. Each channel is represented as a `GetChannelDto` object containing
     * details such as the channel's ID, name, type, and other attributes.
     *
     * @return A list of `GetChannelDto` objects representing the channels in the guild.
     *         The list is non-null but could be empty if the guild has no channels.
     */
    fun getChannels(): List<GetChannelDto>

    /**
     * Creates a new Discord channel in the configured guild.
     *
     * This method sends a request to the Discord API to create a channel based on the supplied parameters.
     *
     * @param channelDto A data transfer object containing the details required to create a new channel,
     * such as its name, type, and optional parent category ID.
     * @return A `GetChannelDto` object containing details of the newly created channel,
     * including its ID, name, type, and associated guild ID.
     */
    fun createChannel(channelDto: CreateChannelDto): GetChannelDto

    /**
     * Retrieves details of a specific Discord channel.
     *
     * @param channelId The unique identifier of the Discord channel to be retrieved.
     * @return A `GetChannelDto` object containing details of the requested channel.
     */
    fun getChannel(channelId: String): GetChannelDto

    /**
     * Retrieves a list of messages from a specific Discord channel.
     *
     * @param channelId The unique identifier of the Discord channel from which messages will be retrieved.
     * @param limit The maximum number of messages to retrieve, default is 50, range is 1-100.
     * @return A list of `GetMessageDto` objects representing the messages in the channel.
     *         The list is non-null but could be empty if the channel has no messages.
     */
    fun getMessages(channelId: String, limit: Int = 50): List<GetMessageDto>

    /**
     * Retrieves details of a specific Discord message.
     *
     * @param channelId The unique identifier of the Discord channel where the message is located.
     * @param messageId The unique identifier of the Discord message to be retrieved.
     * @return A `GetMessageDto` object containing details of the requested message.
     */
    fun getMessage(channelId: String, messageId: String): GetMessageDto

    /**
     * Adds a reaction to a specific Discord message.
     *
     * @param channelId The unique identifier of the Discord channel where the message is located.
     * @param messageId The unique identifier of the Discord message to be reacted to.
     * @param emoji The emoji to be added as a reaction, must be URL encoded if it's a custom emoji.
     */
    fun addReaction(channelId: String, messageId: String, emoji: String)
}