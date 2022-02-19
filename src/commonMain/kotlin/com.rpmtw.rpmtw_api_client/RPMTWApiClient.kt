@file:JvmName("RPMTWApiClient")

package com.rpmtw.rpmtw_api_client

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

private var apiClient: RPMTWApiClient? = null
private var globalToken: String? = null

class RPMTWApiClient(development: Boolean = false, baseUrl: String? = null, token: String? = null) {
    private var apiBaseUrl: String
    val baseUrl: String
        get() = apiBaseUrl

    companion object {
        @JvmStatic
        private val uninitializedException: Exception =
            Exception("Client is not initialized, please call RPMTWApiClient.init() first")

        @JvmStatic
        fun getInstance(): RPMTWApiClient {
            if (apiClient == null) {
                throw uninitializedException
            } else {
                return apiClient!!
            }
        }

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
        fun init(development: Boolean = false, baseUrl: String? = null, token: String? = null) {
            apiClient = RPMTWApiClient(development, baseUrl)
            if (token != null) globalToken = token
        }

        @JvmStatic
        fun setGlobalToken(token: String) {
            globalToken = token
        }
    }


    init {
        apiBaseUrl = baseUrl ?: if (development) "http://0.0.0.0:8080" else "https://api.rpmtw.com:2096"
        apiClient = this
        if (token != null) globalToken = token
    }
}