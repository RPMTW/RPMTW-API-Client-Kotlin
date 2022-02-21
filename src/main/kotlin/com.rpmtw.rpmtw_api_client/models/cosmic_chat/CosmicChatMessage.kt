package com.rpmtw.rpmtw_api_client.models.cosmic_chat

import java.sql.Timestamp

data class CosmicChatMessage(
    val uuid: String,
    val username: String,
    val message: String,
    val nickname: String?,
    val avatarUrl: String,
    /**
     * message sent time (UTC+0)
     */
    val sentAt: Timestamp,
    val userType: CosmicChatUserType,
    val replyMessageUUID: String?
)

@Suppress("EnumEntryName")
enum class CosmicChatUserType {
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