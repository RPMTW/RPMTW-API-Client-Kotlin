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
    fun getBaseUrl() {
        val client = RPMTWApiClient()
        assertEquals(client.baseUrl, "https://api.rpmtw.com:2096")
    }

    @Test
    fun getBaseUrlWithDevelopment() {
        val client = RPMTWApiClient(development = true)
        assertEquals(client.baseUrl, "http://localhost:8080")
    }

    @Test
    fun getBaseUrlCustom() {
        val client = RPMTWApiClient(baseUrl = "http://localhost:1234")
        assertEquals(client.baseUrl, "http://localhost:1234")
    }

    @Test
    fun init() {
        val client = RPMTWApiClient.init()
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
    fun getInstanceUninitialized() {
        val exception: ClientUninitializedException = assertFailsWith(block = {
            @Suppress("UNUSED_VARIABLE")
            val client = RPMTWApiClient.instance
        })
        assertContains(exception.message, "not initialized")
    }
}