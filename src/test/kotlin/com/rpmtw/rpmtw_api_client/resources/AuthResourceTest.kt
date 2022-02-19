package com.rpmtw.rpmtw_api_client.resources

import com.rpmtw.rpmtw_api_client.RPMTWApiClient
import com.rpmtw.rpmtw_api_client.exceptions.ModelNotFoundException
import com.rpmtw.rpmtw_api_client.mock.MockHttpClient
import com.rpmtw.rpmtw_api_client.mock.MockHttpResponse
import com.rpmtw.rpmtw_api_client.models.auth.User
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class AuthResourceTest {
    @BeforeTest
    fun setUp() {
        RPMTWApiClient.clearCache()
        RPMTWApiClient.init()
    }

    @Test
    fun getUserByUUID() {
        val uuid = "d97fce06-ed1f-4acd-8d8e-f3676a1cdeb6";
        val client = RPMTWApiClient.instance
        val mockUser = User(
            uuid = uuid,
            username = "SiongSng",
            email = "rrt467778@gmail.com",
            emailVerified = true
        )
        MockHttpClient.mockRequest(MockHttpResponse(data = mockUser))

        val user: User = client.authResource.getUserByUUID(uuid)
        assertEquals(user, mockUser)
    }

    @Test
    fun getUserByUUIDNotFound() {
        MockHttpClient.mockRequest(MockHttpResponse(statusCode = 404, data = null))

        val exception: ModelNotFoundException = assertFailsWith(block = {
            val client = RPMTWApiClient.instance
            val user: User = client.authResource.getUserByUUID("test")
            println(user.username)
        })
        assertContains(exception.message, "User not found")
    }
}