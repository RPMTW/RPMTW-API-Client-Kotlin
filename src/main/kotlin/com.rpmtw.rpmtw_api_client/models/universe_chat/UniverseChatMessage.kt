package com.rpmtw.rpmtw_api_client.models.universe_chat

import java.sql.Timestamp

data class UniverseChatMessage(
    val uuid: String,
    val username: String,
    /**
     * User identifier
     * May be the RPMTW account uuid, Minecraft account uuid or Discord account id
     * Format: `rpmtw:uuid`, `minecraft:uuid` or `discord:id`
     */
    val userIdentifier: String,
    val message: String,
    val nickname: String?,
    val avatarUrl: String?,
    /**
     * message sent time (UTC+0)
     */
    val sentAt: Timestamp,
    val userType: UniverseChatUserType,
    val replyMessageUUID: String?
)

@Suppress("EnumEntryName")
enum class UniverseChatUserType {
    /**
     * RPMTW account
     */
    rpmtw,

    /**
     * Minecraft account
     */
    minecraft,

    /**
     * Discord account
     */
    discord,
}