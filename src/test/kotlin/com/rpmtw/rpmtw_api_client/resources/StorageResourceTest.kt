package com.rpmtw.rpmtw_api_client.resources

import com.rpmtw.rpmtw_api_client.RPMTWApiClient
import com.rpmtw.rpmtw_api_client.exceptions.FailedGetDataException
import com.rpmtw.rpmtw_api_client.exceptions.ModelNotFoundException
import com.rpmtw.rpmtw_api_client.mock.MockHttpClient
import com.rpmtw.rpmtw_api_client.mock.MockHttpResponse
import com.rpmtw.rpmtw_api_client.models.storage.Storage
import com.rpmtw.rpmtw_api_client.util.TestUtilities
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class StorageResourceTest {
    @BeforeTest
    fun setUp() {
        TestUtilities.setUp()
    }

    @Test
    fun getStorage() {
        val uuid = "c527d55b-a4b7-4065-b6f2-04c164436123"
        val client = RPMTWApiClient.instance

        val mockStorage = Storage(
            uuid = uuid,
            contentType = "image/png",
            type = "general",
            createAt = "1645342282"
        )
        MockHttpClient.mockRequest(MockHttpResponse(data = mockStorage))
        runBlocking {
            val storage: Storage = client.storageResource.getStorage(uuid)
            assertEquals(storage, mockStorage)
        }
    }

    @Test
    fun getStorageNotFound() {
        MockHttpClient.mockRequest(MockHttpResponse(statusCode = 404, data = null))

        val exception: ModelNotFoundException = assertFailsWith(block = {
            val client = RPMTWApiClient.instance
            runBlocking {
                client.storageResource.getStorage("test")
            }
        })
        assertContains(exception.message, "Storage not found")
    }

    @Test
    fun getStorageUnknownException() {
        MockHttpClient.mockRequest(MockHttpResponse(statusCode = 400, data = null, responseMessage = "Bad Request"))

        val exception: FailedGetDataException = assertFailsWith(block = {
            val client = RPMTWApiClient.instance
            runBlocking {
                client.storageResource.getStorage("36a6dcda-7ad0-4e0d-a3e0-35d7e264ce2e")
            }
        })
        assertContains(exception.message, "Failed")
    }
}