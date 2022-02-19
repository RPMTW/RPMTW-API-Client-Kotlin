package com.rpmtw.rpmtw_api_client.models.auth

import com.rpmtw.rpmtw_api_client.mock.MockHttpClient
import com.rpmtw.rpmtw_api_client.mock.MockHttpResponse
import com.rpmtw.rpmtw_api_client.utilities.TestUtilities
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest

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
            kotlin.test.assertEquals(user, mockUser)
        }
    }
}