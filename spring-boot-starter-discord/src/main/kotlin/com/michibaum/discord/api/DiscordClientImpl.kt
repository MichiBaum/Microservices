package com.michibaum.discord.api

import com.michibaum.discord.api.dtos.*
import com.michibaum.discord.config.DiscordProperties
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import org.springframework.web.client.bodyWithType

/**
 * Implementation of the Discord client for making API calls to Discord services.
 *
 * The `DiscordClientImpl` class provides methods to interact with the Discord API, allowing for
 * operations such as retrieving guild information, fetching channel lists, creating channels,
 * and sending messages. It uses an HTTP client configured with necessary headers and base URL
 * specific to the Discord API v10.
 *
 * @property properties Configuration properties containing the bot token and guild ID.
 */
open class DiscordClientImpl(
    private val properties: DiscordProperties
): DiscordClient {

    /**
     * The base URL for the Discord API v10.
     *
     * This URL serves as the entry point for making requests to the Discord API.
     * It includes the version number to specify the API version being used.
     *
     * @see [Discord API Documentation](https://discord.com/developers/docs/reference#api-versioning)
     */
    val url = "https://discord.com/api/v10/"

    /**
     * Represents the authorization token required for accessing the Discord API in this client.
     *
     * The token is prefixed with "Bot " as required by Discord's authentication scheme.
     * This token should be kept confidential and secure, avoiding exposure in version control or logs.
     *
     * @see [Discord API Documentation](https://discord.com/developers/docs/reference#authentication)
     */
    val authToken = "Bot ${properties.botToken}"

    /**
     * A pre-configured REST client used to interact with the Discord API.
     *
     * This client is responsible for making HTTP requests to various Discord API endpoints
     * and includes default settings such as the base URL, authorization token, and headers required
     * for communication with the Discord servers.
     *
     * The `httpClient` is primarily utilized by the `DiscordClientImpl` class to perform operations
     * including retrieving guild information, fetching channel details, creating new channels, and
     * sending messages.
     *
     * Default headers added to each request include:
     * - `Authorization`: Contains the bot token for authentication.
     * - `Content-Type`: Specifies the `application/json` format for requests and responses.
     * - `User-Agent`: Identifies the client, including its version and source.
     */
    private val httpClient: RestClient = RestClient.builder()
            .baseUrl(url)
            .defaultHeaders { headers ->
                headers.add("Authorization", authToken)
                headers.add("Content-Type", "application/json")
                headers.add("User-Agent", "DiscordBot (https://github.com/MichiBaum/Microservices)")
            }.build()


    /**
     * Retrieves detailed information about the guild associated with the Discord client.
     *
     * This method makes an HTTP GET request to the Discord API's `/guilds/{guildId}` endpoint,
     * where `{guildId}` is replaced with the guild ID configured in the client properties.
     * The response is deserialized into a `GetGuildeDto` object, which contains information
     * such as the guild's ID, name, description, features, and other attributes.
     *
     * @return A `GetGuildeDto` object containing information about the guild. The result is non-null
     * but may require additional checks if the guild information cannot be successfully retrieved.
     */
    override fun getGuild() = httpClient.get() // https://docs.discord.com/developers/resources/guild#get-guild
        .uri("/guilds/${properties.guildId}")
        .retrieve()
        .body(object : ParameterizedTypeReference<GetGuildeDto>() {})!! // TODO maybe nullable

    /**
     * Retrieves a list of channels for the configured guild from the Discord API.
     *
     * This method performs an HTTP GET request to the /guilds/{guildId}/channels endpoint, where {guildId}
     * is replaced with the guildId value from the Discord client properties.
     *
     * The request result is deserialized into a list of `GetChannelDto` objects, each representing
     * detailed information about a channel within the guild.
     *
     * @return A list of `GetChannelDto` objects representing the channels of the guild.
     *         The result is non-null but may be empty if the guild has no channels.
     */
    override fun getChannels() = httpClient.get() // https://docs.discord.com/developers/resources/guild#get-guild-channels
        .uri("/guilds/${properties.guildId}/channels")
        .retrieve()
        .body(object : ParameterizedTypeReference<List<GetChannelDto>>() {})!! // TODO maybe nullable

    /**
     * Retrieves details of a specific Discord channel.
     *
     * @param channelId The unique identifier of the Discord channel to be retrieved.
     * @return A `GetChannelDto` object containing details of the requested channel.
     */
    override fun getChannel(channelId: String) = httpClient.get() // https://docs.discord.com/developers/resources/channel#get-channel
        .uri("/channels/$channelId")
        .retrieve()
        .body(object : ParameterizedTypeReference<GetChannelDto>() {})!! // TODO maybe nullable
    
    /**
     * Creates a new Discord channel in the configured guild by making a POST request to the Discord API.
     *
     * @param channelDto The data transfer object containing the details required to create a new channel,
     * including properties such as the channel name, type, and optional parent category ID.
     */
    override fun createChannel(channelDto: CreateChannelDto) = httpClient.post() // https://docs.discord.com/developers/resources/guild#create-guild-channel
        .uri("/guilds/${properties.guildId}/channels")
        .bodyWithType(channelDto)
        .retrieve()
        .body(object : ParameterizedTypeReference<GetChannelDto>() {})!! // TODO maybe nullable

    /**
     * Retrieves a list of messages from a specific Discord channel.
     *
     * @param channelId The unique identifier of the Discord channel from which messages will be retrieved.
     * @param limit The maximum number of messages to retrieve, default is 50, range is 1-100.
     * @return A list of `GetMessageDto` objects representing the messages in the channel.
     */
    override fun getMessages(channelId: String, limit: Int) = httpClient.get() // https://docs.discord.com/developers/resources/message#get-channel-messages
        .uri { builder ->
            require(limit in 1..100) { "limit must be between 1 and 100" }
            builder.path("/channels/$channelId/messages")
                .queryParam("limit", limit)
                .build()
        }
        .retrieve()
        .body(object : ParameterizedTypeReference<List<GetMessageDto>>() {})!! // TODO maybe nullable

    /**
     * Retrieves details of a specific Discord message.
     *
     * @param channelId The unique identifier of the Discord channel where the message is located.
     * @param messageId The unique identifier of the Discord message to be retrieved.
     * @return A `GetMessageDto` object containing details of the requested message.
     */
    override fun getMessage(channelId: String, messageId: String) = httpClient.get() // https://docs.discord.com/developers/resources/message#get-channel-message
        .uri("/channels/$channelId/messages/$messageId")
        .retrieve()
        .body(object : ParameterizedTypeReference<GetMessageDto>() {})!! // TODO maybe nullable

    /**
     * Sends a message to a specified Discord channel.
     *
     * @param channelId The unique identifier of the Discord channel where the message will be sent.
     * @param messageDto A data transfer object containing the content of the message to be sent.
     * @return A `GetMessageDto` object containing details of the sent message, including its ID and content.
     */
    override fun sendMessage(channelId: String, messageDto: CreateMessageDto) = httpClient.post() // https://docs.discord.com/developers/resources/message#create-message
        .uri("/channels/$channelId/messages")
        .bodyWithType(messageDto)
        .retrieve()
        .body(object : ParameterizedTypeReference<GetMessageDto>() {})!! // TODO maybe nullable

    /**
     * Adds a reaction to a specific Discord message.
     *
     * @param channelId The unique identifier of the Discord channel where the message is located.
     * @param messageId The unique identifier of the Discord message to be reacted to.
     * @param emoji The emoji to be added as a reaction, must be URL encoded if it's a custom emoji.
     */
    override fun addReaction(channelId: String, messageId: String, emoji: String) { // https://docs.discord.com/developers/resources/message#create-reaction
        httpClient.put()
            .uri("/channels/{channelId}/messages/{messageId}/reactions/{emoji}/@me", channelId, messageId, emoji)
            .retrieve()
            .toBodilessEntity()
    }
}