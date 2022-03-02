package com.rpmtw.rpmtw_api_client.resources

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.coroutines.awaitString
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.fuel.httpGet
import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.rpmtw.rpmtw_api_client.exceptions.FailedGetDataException
import com.rpmtw.rpmtw_api_client.exceptions.ModelNotFoundException
import com.rpmtw.rpmtw_api_client.models.cosmic_chat.CosmicChatInfo
import com.rpmtw.rpmtw_api_client.models.cosmic_chat.CosmicChatMessage
import com.rpmtw.rpmtw_api_client.utilities.Utilities
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.engineio.client.Transport
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import java.sql.Timestamp
import io.socket.client.Socket as SocketIO


private var socketCache: SocketIO? = null

class CosmicChatResource(
    override val apiBaseUrl: String,
    private val cosmicChatBaseUrl: String,
    override val globalToken: String?,
) : BaseResource {

    val isConnected: Boolean
        get() = socketCache?.connected() ?: false

    /**
     * Connect to the Cosmic Chat server
     * @param minecraftUUID player's minecraft UUID (optional)
     * @param token rpmtw account token (optional)
     * [minecraftUUID], [token] cannot both be empty
     */
    suspend fun connect(minecraftUUID: String? = null, token: String? = null) {
        runBlocking {
            val baseOption = IO.Options()
            baseOption.query
            if (minecraftUUID == null && token == null) {
                throw IllegalArgumentException("minecraftUUID and token cannot both be null")
            }

            baseOption.transports = arrayOf(WebSocket.NAME)
            var socket: SocketIO = IO.socket(cosmicChatBaseUrl, baseOption)

            println("Connecting to Cosmic Chat server...")
            var connected = false
            socket.io().on(Manager.EVENT_TRANSPORT) { args ->
                val transport: Transport = args[0] as Transport
                transport.on(Transport.EVENT_REQUEST_HEADERS) { headerArgs ->
                    @Suppress("UNCHECKED_CAST") val headers = headerArgs[0] as MutableMap<String, List<String>>
                    if (minecraftUUID != null) {
                        headers["minecraft_uuid"] = mutableListOf(minecraftUUID)
                    }
                    if (token != null) {
                        headers["rpmtw_auth_token"] = mutableListOf(token)
                    }
                }
            }
            socket.on(SocketIO.EVENT_CONNECT) {
                println("Connected to Cosmic Chat server")
                connected = true
            }
            socket = socket.connect()


            // Wait for connection
            while (!connected) {
                withContext(Dispatchers.IO) {
                    Thread.sleep(100)
                }
            }
            // Wait for the connection to be initialized finished
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
            socketCache = socket
        }
    }

    /**
     * Disconnect from the Cosmic Chat server
     */
    fun disconnect() {
        socketCache?.disconnect()
        socketCache?.off()
        socketCache = null
    }

    private fun connectCheck() {
        if (socketCache == null) {
            throw IllegalStateException("Not connected to the Cosmic Chat server, call connect() first")
        }
    }

    /**
     * Send a message to the server, and return sent status.
     * @param message message content
     * @param nickname user's nickname
     * @return sent status
     */
    suspend fun sendMessage(
        message: String, nickname: String? = null
    ): String {
        return handleMessage(message = message, nickname = nickname)
    }


    /**
     * Reply message by message uuid, and return the replied status.
     * @param message message content
     * @param uuid message uuid to reply to
     * @param nickname user's nickname
     * @return replied status
     */
    suspend fun replyMessage(message: String, uuid: String, nickname: String? = null): String {
        return handleMessage(message = message, nickname = nickname, replyMessageUUID = uuid)
    }

    private suspend fun handleMessage(
        message: String, nickname: String? = null, replyMessageUUID: String? = null
    ): String {
        connectCheck()
        var status: String? = null

        val json = JsonObject()
        json.addProperty("message", message)
        if (nickname != null) {
            json.addProperty("nickname", nickname)
        }
        if (replyMessageUUID != null) {
            json.addProperty("replyMessageUUID", replyMessageUUID)
        }

        socketCache!!.emit("clientMessage", Gson().toJson(json).toByteArray(Charsets.UTF_8), Ack {
            val response: JsonObject = JsonParser.parseString(it[0] as String).asJsonObject
            status = response.get("status").asString
        })

        while (status == null) {
            withContext(Dispatchers.IO) {
                Thread.sleep(100)
            }
        }
        return status!!
    }

    /**
     * Receive messages sent by other users
     */
    fun onMessageSent(handler: (message: CosmicChatMessage) -> Unit) {
        connectCheck()

        socketCache!!.on("sentMessage") { args ->
            val intList: List<Int> =
                (args[0] as JSONArray).toString().removeSurrounding("[", "]").split(",").map { it.toInt() }
            val jsonBytes: ByteArray =
                intList.foldIndexed(ByteArray(intList.size)) { i, a, v -> a.apply { set(i, v.toByte()) } }
            val json = String(jsonBytes, Charsets.UTF_8)
            val gson: Gson = GsonBuilder().registerTypeAdapter(Timestamp::class.java, TimestampAdapter()).create()
            val message: CosmicChatMessage = gson.fromJson(json, CosmicChatMessage::class.java)
            handler(message)
        }
    }

    /**
     * Get message by message uuid
     * @param uuid message uuid
     */
    @Throws(FailedGetDataException::class)
    suspend fun getMessage(uuid: String): CosmicChatMessage {
        return runBlocking {
            val url = "$apiBaseUrl/cosmic-chat/view/$uuid"
            val request: Request = url.httpGet()

            request.awaitStringResult().fold({
                return@fold Utilities.jsonDeserialize(
                    it,
                    CosmicChatMessage::class.java,
                    gson = GsonBuilder().registerTypeAdapter(Timestamp::class.java, TimestampAdapter()).create()
                )
            }, {
                if (it.response.statusCode == 404) {
                    throw ModelNotFoundException(CosmicChatMessage::class)
                }

                throw FailedGetDataException(it)
            })
        }
    }

    /**
     * Get cosmic chat info (online users, protocolVersion, etc.)
     */
    suspend fun getInfo(): CosmicChatInfo {
        return runBlocking {
            val url = "$apiBaseUrl/cosmic-chat/info"
            val request: Request = url.httpGet()
            return@runBlocking Utilities.jsonDeserialize(request.awaitString(), CosmicChatInfo::class.java)
        }
    }
}

private class TimestampAdapter : TypeAdapter<Timestamp>() {
    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Timestamp {
        return Timestamp(`in`.nextLong())
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, timestamp: Timestamp) {
        out.value(timestamp.time)
    }
}
