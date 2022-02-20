package com.rpmtw.rpmtw_api_client.models.storage

import com.rpmtw.rpmtw_api_client.RPMTWApiClient

data class Storage(val uuid: String, val contentType: String, val type: String, val createAt: String) {

    companion object {
        /**
         * Get download url of the storage
         * @param uuid uuid of the storage
         * @param apiBaseUrl api base url (optional)
         */
        fun getDownloadUrl(uuid: String, apiBaseUrl: String? = null): String {
            val baseUrl: String = apiBaseUrl ?: RPMTWApiClient.instance.apiBaseUrl;
            return "$baseUrl/storage/$uuid/download";
        }
    }
}