package com.rpmtw.rpmtw_api_client.models.auth

data class User(
    val uuid: String,
    val username: String,
    val email: String,
    val emailVerified: Boolean,
    val avatarStorageUUID: String,
) {
//    val avatarUrl: String?
//        get() {
//            if (avatarStorageUUID != null){
//                return Storage.getDownloadUrl(avatarStorageUUID)
//            }else{
//               return null
//            }
//        }
}
