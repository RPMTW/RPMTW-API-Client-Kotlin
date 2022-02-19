package com.rpmtw.rpmtw_api_client.resources

import com.rpmtw.rpmtw_api_client.RPMTWApiClient
import com.rpmtw.rpmtw_api_client.exceptions.ModelNotFoundException
import com.rpmtw.rpmtw_api_client.mock.MockHttpClient
import com.rpmtw.rpmtw_api_client.mock.MockHttpResponse
import com.rpmtw.rpmtw_api_client.models.auth.User
import com.rpmtw.rpmtw_api_client.utilities.TestUtilities
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class AuthResourceTest {
    @BeforeTest
    fun setUp() {
        TestUtilities.setUp()
    }

    @Test
    suspend fun getUserByUUID() {
        val uuid = "d97fce06-ed1f-4acd-8d8e-f3676a1cdeb6"
        val client = RPMTWApiClient.instance

        @Suppress("SpellCheckingInspection")
        val mockUser = User(
            uuid = uuid,
            username = "Lacey Huggins",
            email = "johnathen_finnellp@responses.ma",
            emailVerified = true
        )
        MockHttpClient.mockRequest(MockHttpResponse(data = mockUser))

        val user: User = client.authResource.getUserByUUID(uuid)
        assertEquals(user, mockUser)
    }

    @Test
    suspend fun getUserByUUIDWithToken() {
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkJpdHRhbnkgTGFiZWxsZSIsImlhdCI6MTUxNjIzOTAyMn0.WId7rOn-Xkbc4yJdFUMlcTjpR38xRI1rtIJepYxzZHI"
        val client = RPMTWApiClient.instance

        @Suppress("SpellCheckingInspection")
        val mockUser = User(
            uuid = "d97fce06-ed1f-4acd-8d8e-f3676a1cdeb6",
            username = "Vida Harding",
            email = "tangela_burlison9dj@grace.uu",
            emailVerified = true
        )
        MockHttpClient.mockRequest(MockHttpResponse(data = mockUser))

        val user: User = client.authResource.getUserByUUID("me", token = token)
        assertEquals(user, mockUser)
    }

    @Test
    suspend fun getUserByUUIDNotFound() {
        MockHttpClient.mockRequest(MockHttpResponse(statusCode = 404, data = null))

        val exception: ModelNotFoundException = assertFailsWith(block = {
            val client = RPMTWApiClient.instance
            val user: User = client.authResource.getUserByUUID("test")
            println(user.username)
        })
        assertContains(exception.message, "User not found")
    }

    @Test
    suspend fun getUserByEmail() {
        val uuid = "fcddfd48-e25a-4db8-849c-ac2c3dbce764"
        val client = RPMTWApiClient.instance

        @Suppress("SpellCheckingInspection")
        val mockUser = User(
            uuid = uuid,
            username = "Maricela Orellana",
            email = "phillis_fridleyhb@scuba.kaq",
            emailVerified = true
        )
        MockHttpClient.mockRequest(MockHttpResponse(data = mockUser))

        val user: User = client.authResource.getUserByEmail(uuid)
        assertEquals(user, mockUser)
    }
}