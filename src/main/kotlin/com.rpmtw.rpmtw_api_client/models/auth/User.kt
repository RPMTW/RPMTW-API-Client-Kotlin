package com.rpmtw.rpmtw_api_client.models.auth

import com.rpmtw.rpmtw_api_client.RPMTWApiClient

data class User(
    val uuid: String,
    val username: String,
    val email: String,
    val emailVerified: Boolean,
    val avatarStorageUUID: String? = null,
) {
//    val avatarUrl: String?
//        get() {
//            if (avatarStorageUUID != null){
//                return Storage.getDownloadUrl(avatarStorageUUID)
//            }else{
//               return null
//            }
//        }

    companion object {
        @JvmStatic
        fun getByUUID(uuid: String): User {
            return RPMTWApiClient.instance.authResource.getUserByUUID(uuid)
        }
    }
}