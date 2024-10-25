package com.michibaum.discord.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.discord.api.dtos.*
import com.michibaum.discord.config.DiscordProperties
import reactor.core.publisher.Flux
import reactor.netty.ByteBufFlux
import reactor.netty.http.client.HttpClient

/**
 * Implementation of the Discord client for making API calls to Discord services.
 *
 * The `DiscordClientImpl` class provides methods to interact with the Discord API, allowing for
 * operations such as retrieving guild information, fetching channel lists, creating channels,
 * and sending messages. It uses an HTTP client configured with necessary headers and base URL
 * specific to the Discord API v10.
 *
 * @property properties Configuration properties containing the bot token and guild ID.
 * @property objectMapper ObjectMapper instance for JSON serialization and deserialization.
 */
open class DiscordClientImpl(val properties: DiscordProperties, val objectMapper: ObjectMapper): DiscordClient {

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
     * A configured HTTP client for interacting with the Discord API.
     *
     * This client is set up with a base URL and common headers needed for authentication and
     * content type specification. The headers include:
     * - Authorization: required for API access
     * - Content-Type: set to application/json for JSON data exchange
     * - User-Agent: identifies the client as a Discord bot
     *
     * The client is primarily used for making HTTP requests such as GET and POST to various
     * Discord API endpoints to manage guilds, channels, and other resources.
     */
    val httpClient = HttpClient.create()
        .baseUrl(url)
        .headers { headers ->
            headers.add("Authorization", authToken)
            headers.add("Content-Type", "application/json")
            headers.add("User-Agent", "DiscordBot (https://github.com/MichiBaum/Microservices)")
        }

    /**
     * Retrieves the guild information for the configured guild from the Discord API.
     *
     * This method makes an HTTP GET request to the /guilds/{guildId} endpoint of the Discord API.
     * The response content is aggregated into a single string and then deserialized into a `GetGuildeDto` object
     * using the configured ObjectMapper.
     *
     * @return a `Mono<GetGuildeDto>` representing the guild information.
     */
    override fun getGuild() = httpClient.get()
        .uri("/guilds/${properties.guildId}")
        .responseContent()
        .aggregate()
        .asString()
        .map { x -> objectMapper.readValue(x, GetGuildeDto::class.java) }

    /**
     * Retrieves the list of channels for the configured guild from the Discord API.
     *
     * This method makes an HTTP GET request to the /guilds/{guildId}/channels endpoint of the Discord API.
     * The response content is aggregated into a single string and then deserialized into a list of `GetChannelDto` objects
     * using the configured ObjectMapper.
     *
     * @return a `Mono<List<GetChannelDto>>` representing the list of channels in the guild.
     */
    override fun getChannels() = httpClient.get()
        .uri("/guilds/${properties.guildId}/channels")
        .responseContent()
        .aggregate()
        .asString()
        .map<List<GetChannelDto>> { x -> objectMapper.readValue(x, objectMapper.typeFactory.constructCollectionType(List::class.java, GetChannelDto::class.java)) }

    /**
     * Creates a new Discord channel within the specified guild.
     *
     * @param channelDto The details of the channel to be created, encapsulated in a `CreateChannelDto` object.
     */
    override fun createChannel(channelDto: CreateChannelDto) = httpClient.post()
        .uri("/guilds/${properties.guildId}/channels")
        .send(ByteBufFlux.fromString(Flux.just(objectMapper.writeValueAsString(channelDto))))
        .responseContent()
        .aggregate()
        .asString()
        .map { x -> objectMapper.readValue(x, GetChannelDto::class.java)}

    // Create Message
    /**
     * Sends a message to a specified Discord channel.
     *
     * @param channelId the ID of the channel where the message will be sent.
     * @param messageDto the details of the message to be sent, encapsulated in a `CreateMessageDto` object.
     * @return a `Mono<GetMessageDto>` representing the sent message's details.
     */
    override fun sendMessage(channelId: String, messageDto: CreateMessageDto) = httpClient.post()
        .uri("/channels/$channelId/messages")
        .send(ByteBufFlux.fromString(Flux.just(objectMapper.writeValueAsString(messageDto))))
        .responseContent()
        .aggregate()
        .asString()
        .map { x -> objectMapper.readValue(x, GetMessageDto::class.java) }
    

    // Get Channel
    // https://discord.com/developers/docs/resources/channel#get-channel
}