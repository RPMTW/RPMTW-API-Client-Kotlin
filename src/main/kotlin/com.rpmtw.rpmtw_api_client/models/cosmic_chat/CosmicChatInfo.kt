package com.rpmtw.rpmtw_api_client.models.cosmic_chat

data class CosmicChatInfo(
    /**
     * Number of online users.
     */
    val onlineUsers: Int,
    /**
     * Protocol version. (current version is 1)
     */
    val protocolVersion: Int
)
