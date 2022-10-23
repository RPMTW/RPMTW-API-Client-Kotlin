package com.rpmtw.rpmtw_api_client.models.auth

import com.rpmtw.rpmtw_api_client.RPMTWApiClient
import com.rpmtw.rpmtw_api_client.mock.MockHttpClient
import com.rpmtw.rpmtw_api_client.mock.MockHttpResponse
import com.rpmtw.rpmtw_api_client.util.TestUtilities
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

internal class UserTest {
    @BeforeTest
    fun setUp() {
        TestUtilities.setUp()
    }

    @Test
    fun getByUUID() {
        val uuid = "d97fce06-ed1f-4acd-8d8e-f3676a1cdeb6"

        @Suppress("SpellCheckingInspection")
        val mockUser = User(
            uuid = uuid,
            username = "Lacey Huggins",
            email = "johnathen_finnellp@responses.ma",
            emailVerified = true
        )
        MockHttpClient.mockRequest(MockHttpResponse(data = mockUser))

        runBlocking {
            val user: User = User.getByUUID(uuid)
            assertEquals(user, mockUser)
        }
    }

    @Test
    fun getAvatarUrl() {
        val user = User(
            uuid = "b3affe3c-497d-4fa3-960b-d02e3fed8588",
            username = "India Shelby",
            email = "alvina_cress75pf@linux.cz",
            emailVerified = true,
            avatarStorageUUID = "5999271e-c50c-444f-9b6e-667974334fb7"
        )
        val apiBaseUrl: String = RPMTWApiClient.instance.apiBaseUrl
        assertEquals(user.avatarUrl, "$apiBaseUrl/storage/${user.avatarStorageUUID}/download")
    }

    @Test
    fun getAvatarUrlWithNoneAvatar() {
        val user = User(
            uuid = "b3affe3c-497d-4fa3-960b-d02e3fed8588",
            username = "India Shelby",
            email = "alvina_cress75pf@linux.cz",
            emailVerified = true
        )
        assertEquals(user.avatarUrl, null)
    }
}