package com.rpmtw.rpmtw_api_client.models.universe_chat

data class UniverseChatInfo(
    /**
     * Number of online users.
     */
    val onlineUsers: Int,
    /**
     * Protocol version. (current version is 1)
     */
    val protocolVersion: Int
)
