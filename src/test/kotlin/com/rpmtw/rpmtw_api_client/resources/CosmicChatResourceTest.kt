package com.rpmtw.rpmtw_api_client.resources

import com.rpmtw.rpmtw_api_client.RPMTWApiClient
import com.rpmtw.rpmtw_api_client.exceptions.FailedGetDataException
import com.rpmtw.rpmtw_api_client.exceptions.ModelNotFoundException
import com.rpmtw.rpmtw_api_client.mock.MockHttpClient
import com.rpmtw.rpmtw_api_client.mock.MockHttpResponse
import com.rpmtw.rpmtw_api_client.models.auth.CreateUserResult
import com.rpmtw.rpmtw_api_client.models.cosmic_chat.CosmicChatInfo
import com.rpmtw.rpmtw_api_client.models.cosmic_chat.CosmicChatMessage
import com.rpmtw.rpmtw_api_client.models.cosmic_chat.CosmicChatUserType
import com.rpmtw.rpmtw_api_client.utilities.TestUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Suppress("SpellCheckingInspection")
internal class CosmicChatResourceTest {
    private val minecraftUUID = "977e69fb-0b15-40bf-b25e-4718485bf72f"
    private val minecraftUsername = "SiongSng"
    private val message = "Hello World!"

    @BeforeTest
    fun setUp() {
        TestUtilities.setUp()
    }

    @Test
    fun connectWithMinecraftAccount() {
        val client = RPMTWApiClient.init(development = true)
        runBlocking {
            val resource: CosmicChatResource = client.cosmicChatResource
            resource.connect(minecraftUUID = minecraftUUID)
            assertEquals(resource.isConnected, true)
            client.cosmicChatResource.disconnect()
            assertEquals(resource.isConnected, false)
        }
    }

    @Test
    fun connectWithRPMTWAccount() {
        val client = RPMTWApiClient.init(development = true)
        runBlocking {
            val user: CreateUserResult = client.authResource.createUser(
                username = "Elizabethann Kowalewski",
                password = "uByz1WVDhLM",
                email = "sadae_hirschman8cj@gmail.com"
            )
            val token: String = user.token
            val resource: CosmicChatResource = client.cosmicChatResource
            resource.connect(token = token)
            assertEquals(resource.isConnected, true)
            client.cosmicChatResource.disconnect()
            assertEquals(resource.isConnected, false)
        }
    }

    @Test
    fun connectWithNull() {
        val client = RPMTWApiClient.init(development = true)
        val exception: IllegalArgumentException = assertFailsWith(block = {
            runBlocking {
                val resource: CosmicChatResource = client.cosmicChatResource
                resource.connect()
                assertEquals(resource.isConnected, false)
            }
        })
        assertContains(exception.message!!, "null")
    }

    @Test
    fun sendMessage() {
        val client = RPMTWApiClient.init(development = true)
        val resource: CosmicChatResource = client.cosmicChatResource
        val messages: ArrayList<CosmicChatMessage> = arrayListOf()

        val nickname = "Sarahi"
        runBlocking {
            resource.connect(minecraftUUID = minecraftUUID)
            assertEquals(resource.isConnected, true)
            resource.onMessageSent {
                messages.add(it)
            }
            resource.sendMessage(message = message, nickname = nickname)
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
            assertEquals(messages.isNotEmpty(), true)
            assertEquals(messages.first().message, message)
            assertEquals(messages.first().nickname, nickname)
            assertEquals(messages.first().username, minecraftUsername)
            assertContains(messages.first().avatarUrl, minecraftUUID)
            assertEquals(messages.first().sentAt.before(Date(System.currentTimeMillis())), true)
            assertEquals(messages.first().userType, CosmicChatUserType.minecraft)
            assertEquals(messages.first().replyMessageUUID, null)
            assertEquals(messages.size, 1)

            resource.disconnect()
            assertEquals(resource.isConnected, false)
        }
    }

    @Test
    fun sendMessageNoConnect() {
        val client = RPMTWApiClient.init(development = true)
        val exception: IllegalStateException = assertFailsWith(block = {
            runBlocking {
                client.cosmicChatResource.sendMessage(message = message)
            }
        })
        assertContains(exception.message!!, "Not connected")
    }

    @Test
    fun replyMessage() {
        val client = RPMTWApiClient.init(development = true)
        val resource: CosmicChatResource = client.cosmicChatResource
        val messages: ArrayList<CosmicChatMessage> = arrayListOf()
        runBlocking {
            resource.connect(minecraftUUID = minecraftUUID)
            assertEquals(resource.isConnected, true)
            resource.onMessageSent {
                messages.add(it)
            }
            resource.sendMessage(message = message)
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
            val messageUUID: String = messages.first().uuid
            resource.replyMessage(message = "Reply $message", nickname = "Declan", uuid = messageUUID)
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
            assertEquals(messages.isNotEmpty(), true)
            assertEquals(messages.first().message, message)
            assertEquals(messages.size, 2)

            resource.disconnect()
            assertEquals(resource.isConnected, false)
        }
    }

    @Test
    fun replyMessageNoConnect() {
        val client = RPMTWApiClient.init(development = true)
        val resource: CosmicChatResource = client.cosmicChatResource
        val messages: ArrayList<CosmicChatMessage> = arrayListOf()
        val exception: IllegalStateException = assertFailsWith(block = {
            runBlocking {
                resource.connect(minecraftUUID = minecraftUUID)
                assertEquals(resource.isConnected, true)
                resource.onMessageSent {
                    messages.add(it)
                }
                @Suppress("SpellCheckingInspection")
                resource.sendMessage(message = message, nickname = "Brooklyn")
                withContext(Dispatchers.IO) {
                    Thread.sleep(1000)
                }
                resource.disconnect()
                val messageUUID: String = messages.first().uuid
                resource.replyMessage(
                    message = "Reply $message",
                    nickname = "Declan",
                    uuid = messageUUID
                )
            }
        })
        assertContains(exception.message!!, "Not connected")
    }


    @Test
    fun onMessageSentNoConnect() {
        val client = RPMTWApiClient.init(development = true)
        val exception: IllegalStateException = assertFailsWith(block = {
            runBlocking {
                client.cosmicChatResource.onMessageSent {}
            }
        })
        assertContains(exception.message!!, "Not connected")
    }

    @Test
    fun getMessage() {
        val uuid = "d63f30e9-77c4-4191-9ee1-e72257c9e804"
        val client = RPMTWApiClient.instance
        val mockMessage = CosmicChatMessage(
            uuid = uuid,
            username = "Maleah Lasky",
            message = "Hello, world!",
            nickname = "Meyer",
            avatarUrl = "https://deadlyjptynabmauzn.id",
            sentAt = Timestamp(1645628537000),
            userType = CosmicChatUserType.minecraft,
            replyMessageUUID = "94249836-e9e5-4c6f-9436-6655a0e111d8"
        )
        MockHttpClient.mockRequest(MockHttpResponse(data = mockMessage))

        runBlocking {
            val message: CosmicChatMessage = client.cosmicChatResource.getMessage(uuid)
            assertEquals(message, mockMessage)
        }
    }

    @Test
    fun getMessageNotFound() {
        MockHttpClient.mockRequest(MockHttpResponse(statusCode = 404, data = null))

        val exception: ModelNotFoundException = assertFailsWith(block = {
            val client = RPMTWApiClient.instance
            runBlocking {
                client.cosmicChatResource.getMessage("4b45b0e9-5d72-4baf-9652-b7d09248aa99")
            }
        })
        assertContains(exception.message, "CosmicChatMessage not found")
    }

    @Test
    fun getMessageUnknownException() {
        MockHttpClient.mockRequest(MockHttpResponse(statusCode = 400, data = null, responseMessage = "Bad Request"))

        val exception: FailedGetDataException = assertFailsWith(block = {
            val client = RPMTWApiClient.instance
            runBlocking {
                client.cosmicChatResource.getMessage("9069bf32-201f-4530-8270-6fea1094ca7d")
            }
        })
        assertContains(exception.message, "Failed")
    }

    @Test
    fun getInfo() {
        val client = RPMTWApiClient.instance

        val mockInfo = CosmicChatInfo(
            onlineUsers = 100,
            protocolVersion = 1
        )
        MockHttpClient.mockRequest(MockHttpResponse(data = mockInfo))

        runBlocking {
            val info: CosmicChatInfo = client.cosmicChatResource.getInfo()
            assertEquals(info, mockInfo)
        }
    }
}