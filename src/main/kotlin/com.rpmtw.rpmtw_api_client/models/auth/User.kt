package com.rpmtw.rpmtw_api_client.models.auth

import com.rpmtw.rpmtw_api_client.RPMTWApiClient
import com.rpmtw.rpmtw_api_client.models.storage.Storage

data class User(
    val uuid: String,
    val username: String,
    val email: String,
    val emailVerified: Boolean,
    val avatarStorageUUID: String? = null,
) {
    /**
     * Get the user's avatar url.
     * if the user has no avatar, it will return null.
     * @see Storage.getDownloadUrl
     */
    val avatarUrl: String?
        get() {
            return if (avatarStorageUUID != null) {
                Storage.getDownloadUrl(avatarStorageUUID)
            } else {
                null
            }
        }

    companion object {
        @JvmStatic
        suspend fun getByUUID(uuid: String): User {
            return RPMTWApiClient.instance.authResource.getUserByUUID(uuid)
        }
    }
}
