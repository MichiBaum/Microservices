package com.michibaum.discord.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class DiscordMessageContentBuilderTest {

    @Test
    fun testBasicText() {
        val content = DiscordMessageContentBuilder()
            .append("Hello ")
            .append("World")
            .build()
        assertEquals("Hello World", content)
    }

    @Test
    fun testFormatting() {
        val content = DiscordMessageContentBuilder()
            .bold("Bold")
            .append(" ")
            .italic("Italic")
            .append(" ")
            .underline("Underline")
            .append(" ")
            .strikethrough("Strikethrough")
            .append(" ")
            .spoiler("Spoiler")
            .build()
        assertEquals("**Bold** *Italic* __Underline__ ~~Strikethrough~~ ||Spoiler||", content)
    }

    @Test
    fun testNestedFormatting() {
        val content = DiscordMessageContentBuilder.build {
            bold {
                append("Bold ")
                italic("and Italic")
            }
        }
        assertEquals("**Bold *and Italic***", content)
    }

    @Test
    fun testCodeBlocks() {
        val content = DiscordMessageContentBuilder()
            .inlineCode("val x = 1")
            .newLine()
            .codeBlock("println(\"Hello\")", "kotlin")
            .build()
        assertEquals("`val x = 1`\n```kotlin\nprintln(\"Hello\")\n```", content)
    }

    @Test
    fun testMentions() {
        val content = DiscordMessageContentBuilder()
            .userMention("123")
            .append(" ")
            .roleMention("456")
            .append(" ")
            .channelMention("789")
            .append(" ")
            .everyone()
            .append(" ")
            .here()
            .build()
        assertEquals("<@123> <@&456> <#789> @everyone @here", content)
    }

    @Test
    fun testTimestamp() {
        val content = DiscordMessageContentBuilder()
            .timestamp(1625043600L, TimestampStyle.SHORT_DATE_TIME)
            .build()
        assertEquals("<t:1625043600:f>", content)
    }

    @Test
    fun testBlockQuote() {
        val content = DiscordMessageContentBuilder()
            .blockQuote("Single line quote")
            .newLine()
            .multiLineBlockQuote("Multi line\nquote")
            .build()
        assertEquals("> Single line quote\n>>> Multi line\nquote", content)
    }

    @Test
    fun testLists() {
        val content = DiscordMessageContentBuilder()
            .unorderedListItem("Item 1")
            .newLine()
            .unorderedListItem("Item 2")
            .newLine()
            .orderedListItem(1, "First")
            .newLine()
            .orderedListItem(2, "Second")
            .build()
        assertEquals("- Item 1\n- Item 2\n1. First\n2. Second", content)
    }

    @Test
    fun testBoldItalic() {
        val content = DiscordMessageContentBuilder()
            .boldItalic("Bold Italic")
            .build()
        assertEquals("***Bold Italic***", content)
    }
    @Test
    fun `timestamp with instant and ISO_LIKE_DATE_TIME`() {
        val instant = Instant.parse("2026-03-30T09:56:55Z")
        val result = DiscordMessageContentBuilder.build {
            timestamp(instant, TimestampStyle.ISO_LIKE_DATE_TIME)
        }
        assertEquals("2026-03-30 09:56:55", result)
    }

    @Test
    fun `timestamp with instant and style`() {
        val instant = Instant.ofEpochSecond(1625050890)
        val result = DiscordMessageContentBuilder.build {
            timestamp(instant, TimestampStyle.SHORT_TIME)
        }
        assertEquals("<t:1625050890:t>", result)
    }

}
