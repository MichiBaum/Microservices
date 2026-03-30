package com.michibaum.discord.api

/**
 * Fluent API for creating Discord message content with markdown formatting.
 * Based on Discord's markdown documentation: https://docs.discord.com/developers/reference#message-formatting
 */
class DiscordMessageContentBuilder {
    private val content = StringBuilder()

    /**
     * Appends a header (level 1, # text).
     */
    fun header1(text: String): DiscordMessageContentBuilder = apply {
        content.append("# ").append(text)
    }

    /**
     * Appends a header (level 2, ## text).
     */
    fun header2(text: String): DiscordMessageContentBuilder = apply {
        content.append("## ").append(text)
    }

    /**
     * Appends a header (level 3, ### text).
     */
    fun header3(text: String): DiscordMessageContentBuilder = apply {
        content.append("### ").append(text)
    }

    /**
     * Appends plain text to the message.
     */
    fun append(text: String): DiscordMessageContentBuilder = apply {
        content.append(text)
    }

    /**
     * Appends a newline to the message.
     */
    fun newLine(): DiscordMessageContentBuilder = apply {
        content.append("\n")
    }

    /**
     * Appends text in italic (*text*).
     */
    fun italic(text: String): DiscordMessageContentBuilder = apply {
        content.append("*").append(text).append("*")
    }

    /**
     * Appends formatted content in italic.
     */
    fun italic(block: DiscordMessageContentBuilder.() -> Unit): DiscordMessageContentBuilder = apply {
        content.append("*")
        this.block()
        content.append("*")
    }

    /**
     * Appends text in bold (**text**).
     */
    fun bold(text: String): DiscordMessageContentBuilder = apply {
        content.append("**").append(text).append("**")
    }

    /**
     * Appends formatted content in bold.
     */
    fun bold(block: DiscordMessageContentBuilder.() -> Unit): DiscordMessageContentBuilder = apply {
        content.append("**")
        this.block()
        content.append("**")
    }

    /**
     * Appends text with underline (__text__).
     */
    fun underline(text: String): DiscordMessageContentBuilder = apply {
        content.append("__").append(text).append("__")
    }

    /**
     * Appends formatted content with underline.
     */
    fun underline(block: DiscordMessageContentBuilder.() -> Unit): DiscordMessageContentBuilder = apply {
        content.append("__")
        this.block()
        content.append("__")
    }

    /**
     * Appends text with strikethrough (~~text~~).
     */
    fun strikethrough(text: String): DiscordMessageContentBuilder = apply {
        content.append("~~").append(text).append("~~")
    }

    /**
     * Appends formatted content with strikethrough.
     */
    fun strikethrough(block: DiscordMessageContentBuilder.() -> Unit): DiscordMessageContentBuilder = apply {
        content.append("~~")
        this.block()
        content.append("~~")
    }

    /**
     * Appends text as a spoiler (||text||).
     */
    fun spoiler(text: String): DiscordMessageContentBuilder = apply {
        content.append("||").append(text).append("||")
    }

    /**
     * Appends formatted content as a spoiler.
     */
    fun spoiler(block: DiscordMessageContentBuilder.() -> Unit): DiscordMessageContentBuilder = apply {
        content.append("||")
        this.block()
        content.append("||")
    }

    /**
     * Appends text in bold and italic (***text***).
     */
    fun boldItalic(text: String): DiscordMessageContentBuilder = apply {
        content.append("***").append(text).append("***")
    }

    /**
     * Appends formatted content in bold and italic.
     */
    fun boldItalic(block: DiscordMessageContentBuilder.() -> Unit): DiscordMessageContentBuilder = apply {
        content.append("***")
        this.block()
        content.append("***")
    }

    /**
     * Appends text as inline code (`text`).
     */
    fun inlineCode(text: String): DiscordMessageContentBuilder = apply {
        content.append("`").append(text).append("`")
    }

    /**
     * Appends text as a code block (```language\ntext\n```).
     */
    fun codeBlock(text: String, language: String? = null): DiscordMessageContentBuilder = apply {
        content.append("```")
        if (language != null) {
            content.append(language)
        }
        content.append("\n").append(text).append("\n```")
    }

    /**
     * Appends a block quote (> text).
     */
    fun blockQuote(text: String): DiscordMessageContentBuilder = apply {
        content.append("> ").append(text)
    }

    /**
     * Appends a multi-line block quote (>>> text).
     */
    fun multiLineBlockQuote(text: String): DiscordMessageContentBuilder = apply {
        content.append(">>> ").append(text)
    }

    /**
     * Appends an unordered list item (- text).
     */
    fun unorderedListItem(text: String): DiscordMessageContentBuilder = apply {
        content.append("- ").append(text)
    }

    /**
     * Appends an ordered list item (number. text).
     */
    fun orderedListItem(number: Int, text: String): DiscordMessageContentBuilder = apply {
        content.append(number).append(". ").append(text)
    }

    /**
     * Mentions a user (<@userId>).
     */
    fun userMention(userId: String): DiscordMessageContentBuilder = apply {
        content.append("<@").append(userId).append(">")
    }

    /**
     * Mentions a role (<@&roleId>).
     */
    fun roleMention(roleId: String): DiscordMessageContentBuilder = apply {
        content.append("<@&").append(roleId).append(">")
    }

    /**
     * Mentions a channel (<#channelId>).
     */
    fun channelMention(channelId: String): DiscordMessageContentBuilder = apply {
        content.append("<#").append(channelId).append(">")
    }

    /**
     * Mentions @everyone.
     */
    fun everyone(): DiscordMessageContentBuilder = apply {
        content.append("@everyone")
    }

    /**
     * Mentions @here.
     */
    fun here(): DiscordMessageContentBuilder = apply {
        content.append("@here")
    }

    /**
     * Appends a timestamp (<t:unixTimestamp:style>).
     */
    fun timestamp(unixTimestamp: Long, style: TimestampStyle? = null): DiscordMessageContentBuilder = apply {
        content.append("<t:").append(unixTimestamp)
        if (style != null) {
            content.append(":").append(style.code)
        }
        content.append(">")
    }

    /**
     * Builds and returns the final message content as a string.
     */
    fun build(): String {
        return content.toString()
    }

    companion object {
        /**
         * Utility function to create a discord message content using a DSL.
         */
        fun build(block: DiscordMessageContentBuilder.() -> Unit): String {
            return DiscordMessageContentBuilder().apply(block).build()
        }
    }
}

/**
 * Available styles for Discord timestamps.
 */
enum class TimestampStyle(val code: String) {
    /**
     * Short Time (e.g., 9:41 PM)
     */
    SHORT_TIME("t"),
    /**
     * Long Time (e.g., 9:41:30 PM)
     */
    LONG_TIME("T"),
    /**
     * Short Date (e.g., 30/06/2021)
     */
    SHORT_DATE("d"),
    /**
     * Long Date (e.g., 30 June 2021)
     */
    LONG_DATE("D"),
    /**
     * Short Date/Time (e.g., 30 June 2021 9:41 PM)
     */
    SHORT_DATE_TIME("f"),
    /**
     * Long Date/Time (e.g., Wednesday, 30 June 2021 9:41 PM)
     */
    LONG_DATE_TIME("F"),
    /**
     * Relative Time (e.g., 2 months ago)
     */
    RELATIVE_TIME("R")
}
