package com.rpmtw.rpmtw_api_client

import kotlin.jvm.JvmStatic

private var apiClient: RPMTWApiClient? = null

class RPMTWApiClient(development: Boolean = false, baseUrl: String? = null, token: String? = null) {
    private var apiBaseUrl: String
    private var globalToken: String? = null
    val baseUrl: String
        get() = apiBaseUrl

    companion object {
        @JvmStatic
        private val uninitializedException: Exception =
            Exception("Client is not initialized, please call RPMTWApiClient.init() first")

        @JvmStatic
        val instance: RPMTWApiClient
            get() {
                if (apiClient == null) {
                    throw uninitializedException
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
    }

    fun setGlobalToken(token: String) {
        globalToken = token
    }

    val apiGlobalToken: String?
        get() = globalToken


    init {
        apiBaseUrl = baseUrl ?: if (development) "http://localhost:8080" else "https://api.rpmtw.com:2096"
        apiClient = this
        if (token != null) globalToken = token
    }
}