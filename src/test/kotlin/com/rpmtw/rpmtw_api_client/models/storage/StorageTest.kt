package com.rpmtw.rpmtw_api_client.models.storage

import com.rpmtw.rpmtw_api_client.RPMTWApiClient
import com.rpmtw.rpmtw_api_client.utilities.TestUtilities
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

internal class StorageTest {
    @BeforeTest
    fun setUp() {
        TestUtilities.setUp()
    }

    @Test
    fun getDownloadUrl() {
        val storage = Storage(
            uuid = "317eaee1-d029-4f8c-afb3-d372c04725d6",
            contentType = "image/png",
            type = "general",
            createAt = "1645342282"
        )
        assertEquals(
            Storage.getDownloadUrl(storage.uuid),
            "${RPMTWApiClient.instance.apiBaseUrl}/storage/${storage.uuid}/download"
        )
    }

    @Test
    fun getDownloadUrlWithCustomUrl() {
        val storage = Storage(
            uuid = "317eaee1-d029-4f8c-afb3-d372c04725d6",
            contentType = "image/png",
            type = "general",
            createAt = "1645342282"
        )
        val apiBaseUrl = "http://localhost:1234"
        assertEquals(
            Storage.getDownloadUrl(storage.uuid, apiBaseUrl = apiBaseUrl),
            "$apiBaseUrl/storage/${storage.uuid}/download"
        )
    }
}