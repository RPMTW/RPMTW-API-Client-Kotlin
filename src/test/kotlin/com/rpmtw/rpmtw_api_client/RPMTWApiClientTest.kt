package com.rpmtw.rpmtw_api_client

import com.rpmtw.rpmtw_api_client.exceptions.ClientUninitializedException
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class RPMTWApiClientTest {
    @BeforeTest
    fun setUp() {
        RPMTWApiClient.clearCache()
    }

    @Test
    fun getApiBaseUrl() {
        val client = RPMTWApiClient()
        assertEquals(client.apiBaseUrl, "https://api.rpmtw.com:2096")
    }

    @Test
    fun getApiBaseUrlWithDevelopment() {
        val client = RPMTWApiClient(development = true)
        assertEquals(client.apiBaseUrl, "http://localhost:8080")
    }


    @Test
    fun getApiBaseUrlCustom() {
        val client = RPMTWApiClient(apiBaseUrl = "http://localhost:1234")
        assertEquals(client.apiBaseUrl, "http://localhost:1234")
    }

    @Test
    fun getUniverseChatBaseUrl() {
        val client = RPMTWApiClient()
        assertEquals(client.universeChatBaseUrl, "https://api.rpmtw.com:2087")
    }

    @Test
    fun getUniverseChatBaseUrlWithDevelopment() {
        val client = RPMTWApiClient(development = true)
        assertEquals(client.universeChatBaseUrl, "http://localhost:2087")
    }

    @Test
    fun getUniverseChatBaseUrlCustom() {
        val client = RPMTWApiClient(universeChatBaseUrl = "http://localhost:2345")
        assertEquals(client.universeChatBaseUrl, "http://localhost:2345")
    }


    @Test
    fun init() {
        val client = RPMTWApiClient.init()
        assertEquals(client, RPMTWApiClient.instance)
    }

    @Test
    fun initWithToken() {
        @Suppress("SpellCheckingInspection")
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IlRlcmVuY2UgTWVuZGVsIiwiaWF0IjoxNTE2MjM5MDIyfQ.KLB9CRh2C43pD6Fz37EswpYtR0SO8HSuxpseOR2UxGw"
        val client = RPMTWApiClient.init(token = token)
        assertEquals(client, RPMTWApiClient.instance)
    }

    @Test
    fun setGlobalToken() {
        val client = RPMTWApiClient()

        @Suppress("SpellCheckingInspection")
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        client.setGlobalToken(token)
        assertEquals(client.apiGlobalToken, token)
    }

    @Test
    fun getInstance() {
        val client = RPMTWApiClient()
        assertEquals(client, RPMTWApiClient.instance)
    }

    @Test
    fun getInstanceWithToken() {
        @Suppress("SpellCheckingInspection")
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IlRlcmVuY2UgTWVuZGVsIiwiaWF0IjoxNTE2MjM5MDIyfQ.KLB9CRh2C43pD6Fz37EswpYtR0SO8HSuxpseOR2UxGw"
        val client = RPMTWApiClient(token = token)
        assertEquals(client, RPMTWApiClient.instance)
    }

    @Test
    fun getInstanceUninitialized() {
        val exception: ClientUninitializedException = assertFailsWith(block = {
            @Suppress("UNUSED_VARIABLE")
            val client = RPMTWApiClient.instance
        })
        assertContains(exception.message, "not initialized")
    }
}