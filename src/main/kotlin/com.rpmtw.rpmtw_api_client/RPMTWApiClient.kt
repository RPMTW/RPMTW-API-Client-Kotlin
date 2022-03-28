package com.rpmtw.rpmtw_api_client

import com.rpmtw.rpmtw_api_client.exceptions.ClientUninitializedException
import com.rpmtw.rpmtw_api_client.resources.AuthResource
import com.rpmtw.rpmtw_api_client.resources.UniverseChatResource
import com.rpmtw.rpmtw_api_client.resources.StorageResource

private var apiClient: RPMTWApiClient? = null

class RPMTWApiClient(
    development: Boolean = false,
    apiBaseUrl: String? = null,
    universeChatBaseUrl: String? = null,
    token: String? = null
) {
    private var _apiBaseUrl: String
    private var _universeChatBaseUrl: String
    private var globalToken: String? = null
    val apiBaseUrl: String
        get() = _apiBaseUrl

    val universeChatBaseUrl: String
        get() = _universeChatBaseUrl

    val apiGlobalToken: String?
        get() = globalToken

    init {
        _apiBaseUrl = apiBaseUrl ?: if (development) "http://localhost:8080" else "https://api.rpmtw.com:2096"
        _universeChatBaseUrl =
            universeChatBaseUrl ?: if (development) "http://localhost:2087" else "https://api.rpmtw.com:2087"
        apiClient = this
        if (token != null) globalToken = token
    }

    companion object {
        @JvmStatic
        val instance: RPMTWApiClient
            get() {
                if (apiClient == null) {
                    throw ClientUninitializedException()
                } else {
                    return apiClient!!
                }
            }

        @JvmStatic
        fun init(development: Boolean = false, baseUrl: String? = null, token: String? = null): RPMTWApiClient {
            val client = RPMTWApiClient(development, baseUrl, token)
            apiClient = client
            return client
        }

        fun clearCache() {
            apiClient = null
        }
    }

    fun setGlobalToken(token: String) {
        globalToken = token
    }

    val authResource
        get() = AuthResource(_apiBaseUrl, globalToken)
    val storageResource
        get() = StorageResource(_apiBaseUrl, globalToken)
    val universeChatResource
        get() = UniverseChatResource(_apiBaseUrl, _universeChatBaseUrl, globalToken)
}