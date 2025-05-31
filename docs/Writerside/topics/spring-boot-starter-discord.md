# spring-boot-starter-discord

[Discord Documentation](https://discord.com/developers/docs/intro)

## Content
- Creates a Discord Client
- Send logs to your configured discord guild channel.

## Properties
```yaml
spring:
    microservices:
        discord:
            enabled: true
            bot-token: ${DISCORD_BOT_TOKEN}
            guild-id: ${DISCORD_GUILD_ID}
            logging:
                enabled: true
                channel-id: ${DISCORD_CHANNEL}
```