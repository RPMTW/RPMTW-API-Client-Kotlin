package com.rpmtw.rpmtw_api_client.resources

import com.google.gson.GsonBuilder
import com.rpmtw.rpmtw_api_client.RPMTWApiClient
import com.rpmtw.rpmtw_api_client.exceptions.FailedGetDataException
import com.rpmtw.rpmtw_api_client.exceptions.ModelNotFoundException
import com.rpmtw.rpmtw_api_client.mock.MockHttpClient
import com.rpmtw.rpmtw_api_client.mock.MockHttpResponse
import com.rpmtw.rpmtw_api_client.models.auth.CreateUserResult
import com.rpmtw.rpmtw_api_client.models.universe_chat.UniverseChatInfo
import com.rpmtw.rpmtw_api_client.models.universe_chat.UniverseChatMessage
import com.rpmtw.rpmtw_api_client.models.universe_chat.UniverseChatUserType
import com.rpmtw.rpmtw_api_client.models.gson.adapters.TimestampAdapter
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
internal class UniverseChatResourceTest {
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
            val resource: UniverseChatResource = client.universeChatResource
            resource.connect(minecraftUUID = minecraftUUID)
            assertEquals(resource.isConnected, true)
            client.universeChatResource.disconnect()
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
            val resource: UniverseChatResource = client.universeChatResource
            resource.connect(token = token)
            assertEquals(resource.isConnected, true)
            client.universeChatResource.disconnect()
            assertEquals(resource.isConnected, false)
        }
    }

    @Test
    fun connectWithNull() {
        val client = RPMTWApiClient.init(development = true)
        val exception: IllegalArgumentException = assertFailsWith(block = {
            runBlocking {
                val resource: UniverseChatResource = client.universeChatResource
                resource.connect()
                assertEquals(resource.isConnected, false)
            }
        })
        assertContains(exception.message!!, "null")
    }

    @Test
    fun sendMessage() {
        val client = RPMTWApiClient.init(development = true)
        val resource: UniverseChatResource = client.universeChatResource
        val messages: ArrayList<UniverseChatMessage> = arrayListOf()

        val nickname = "Sarahi"
        runBlocking {
            resource.connect(minecraftUUID = minecraftUUID)
            assertEquals(resource.isConnected, true)
            resource.onMessageSent({
                messages.add(it)
            })
            val status: String = resource.sendMessage(message = message, nickname = nickname)
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
            assertEquals(messages.isNotEmpty(), true)
            assertEquals(messages.first().message, message)
            assertEquals(messages.first().nickname, nickname)
            assertEquals(messages.first().username, minecraftUsername)
            messages.first().avatarUrl?.let { assertContains(it, minecraftUUID) }
            assertEquals(messages.first().sentAt.before(Date(System.currentTimeMillis())), true)
            assertEquals(messages.first().userType, UniverseChatUserType.minecraft)
            assertEquals(messages.first().replyMessageUUID, null)
            assertEquals(messages.size, 1)
            assertEquals(status, "success")

            resource.disconnect()
            assertEquals(resource.isConnected, false)
        }
    }

    @Test
    fun sendMessageNoConnect() {
        val client = RPMTWApiClient.init(development = true)
        val exception: IllegalStateException = assertFailsWith(block = {
            runBlocking {
                client.universeChatResource.sendMessage(message = message)
            }
        })
        assertContains(exception.message!!, "Not connected")
    }

    @Test
    fun replyMessage() {
        val client = RPMTWApiClient.init(development = true)
        val resource: UniverseChatResource = client.universeChatResource
        val messages: ArrayList<UniverseChatMessage> = arrayListOf()
        runBlocking {
            resource.connect(minecraftUUID = minecraftUUID)
            assertEquals(resource.isConnected, true)
            resource.onMessageSent({
                messages.add(it)
            })
            val status1: String = resource.sendMessage(message = message)
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
            assertEquals(status1, "success")
            val messageUUID: String = messages.first().uuid
            val status2: String =
                resource.replyMessage(message = "Reply $message", nickname = "Declan", uuid = messageUUID)
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
            assertEquals(messages.isNotEmpty(), true)
            assertEquals(messages.first().message, message)
            assertEquals(messages.size, 2)
            assertEquals(status2, "success")

            resource.disconnect()
            assertEquals(resource.isConnected, false)
        }
    }

    @Test
    fun replyMessageNoConnect() {
        val client = RPMTWApiClient.init(development = true)
        val resource: UniverseChatResource = client.universeChatResource
        val messages: ArrayList<UniverseChatMessage> = arrayListOf()
        val exception: IllegalStateException = assertFailsWith(block = {
            runBlocking {
                resource.connect(minecraftUUID = minecraftUUID)
                assertEquals(resource.isConnected, true)
                resource.onMessageSent({
                    messages.add(it)
                })
                val status: String = resource.sendMessage(message = message, nickname = "Brooklyn")
                withContext(Dispatchers.IO) {
                    Thread.sleep(1000)
                }
                assertEquals(status, "success")
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
                client.universeChatResource.onMessageSent({})
            }
        })
        assertContains(exception.message!!, "Not connected")
    }

    @Test
    fun onMessageSentWithMinecraftFormatting() {
        val client = RPMTWApiClient.init(development = true)
        val resource: UniverseChatResource = client.universeChatResource
        val messages: ArrayList<UniverseChatMessage> = arrayListOf()

        val nickname = "Ashleen"
        runBlocking {
            resource.connect(minecraftUUID = minecraftUUID)
            assertEquals(resource.isConnected, true)
            resource.onMessageSent({
                messages.add(it)
            }, UniverseChatMessageFormat.MinecraftFormatting)
            val status: String = resource.sendMessage(message = "**Bold**", nickname = nickname)
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
            assertEquals(messages.isNotEmpty(), true)
            assertEquals(messages.first().message, "§lBold§r")
            assertEquals(messages.first().nickname, nickname)
            assertEquals(messages.first().username, minecraftUsername)
            messages.first().avatarUrl?.let { assertContains(it, minecraftUUID) }
            assertEquals(messages.first().sentAt.before(Date(System.currentTimeMillis())), true)
            assertEquals(messages.first().userType, UniverseChatUserType.minecraft)
            assertEquals(messages.first().replyMessageUUID, null)
            assertEquals(messages.size, 1)
            assertEquals(status, "success")

            resource.disconnect()
            assertEquals(resource.isConnected, false)
        }
    }


    @Test
    fun getMessage() {
        val uuid = "d63f30e9-77c4-4191-9ee1-e72257c9e804"
        val client = RPMTWApiClient.instance
        val mockMessage = UniverseChatMessage(
            uuid = uuid,
            username = "Maleah Lasky",
            message = "Hello, world!",
            nickname = "Meyer",
            avatarUrl = "https://deadlyjptynabmauzn.id",
            sentAt = Timestamp(1645628537000),
            userType = UniverseChatUserType.minecraft,
            replyMessageUUID = "94249836-e9e5-4c6f-9436-6655a0e111d8"
        )
        MockHttpClient.mockRequest(
            MockHttpResponse(data = mockMessage),
            gson = GsonBuilder().registerTypeAdapter(Timestamp::class.java, TimestampAdapter()).create()
        )

        runBlocking {
            val message: UniverseChatMessage = client.universeChatResource.getMessage(uuid)
            assertEquals(message, mockMessage)
        }
    }

    @Test
    fun getMessageNotFound() {
        MockHttpClient.mockRequest(MockHttpResponse(statusCode = 404, data = null))

        val exception: ModelNotFoundException = assertFailsWith(block = {
            val client = RPMTWApiClient.instance
            runBlocking {
                client.universeChatResource.getMessage("4b45b0e9-5d72-4baf-9652-b7d09248aa99")
            }
        })
        assertContains(exception.message, "UniverseChatMessage not found")
    }

    @Test
    fun getMessageUnknownException() {
        MockHttpClient.mockRequest(MockHttpResponse(statusCode = 400, data = null, responseMessage = "Bad Request"))

        val exception: FailedGetDataException = assertFailsWith(block = {
            val client = RPMTWApiClient.instance
            runBlocking {
                client.universeChatResource.getMessage("9069bf32-201f-4530-8270-6fea1094ca7d")
            }
        })
        assertContains(exception.message, "Failed")
    }

    @Test
    fun getInfo() {
        val client = RPMTWApiClient.instance

        val mockInfo = UniverseChatInfo(
            onlineUsers = 100,
            protocolVersion = 1
        )
        MockHttpClient.mockRequest(MockHttpResponse(data = mockInfo))

        runBlocking {
            val info: UniverseChatInfo = client.universeChatResource.getInfo()
            assertEquals(info, mockInfo)
        }
    }
}