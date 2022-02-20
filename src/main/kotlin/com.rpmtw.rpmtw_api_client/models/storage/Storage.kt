package com.rpmtw.rpmtw_api_client.models.storage

import com.rpmtw.rpmtw_api_client.RPMTWApiClient

data class Storage(val uuid: String) {

    companion object {
        fun getDownloadUrl(uuid: String, apiBaseUrl: String? = null): String {
            val baseUrl: String = apiBaseUrl ?: RPMTWApiClient.instance.apiBaseUrl;
            return "$baseUrl/storage/$uuid/download";
        }
    }
}