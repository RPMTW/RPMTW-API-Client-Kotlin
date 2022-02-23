package com.rpmtw.rpmtw_api_client.resources

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.rpmtw.rpmtw_api_client.models.cosmic_chat.CosmicChatMessage
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
) :
    BaseResource {

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
                    @Suppress("UNCHECKED_CAST")
                    val headers = headerArgs[0] as MutableMap<String, List<String>>
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
     * Send a message to the Cosmic Chat server
     * @param message message content
     * @param nickname user's nickname
     */
    fun sendMessage(message: String, nickname: String? = null) {
        connectCheck()

        val json = JsonObject()
        json.addProperty("message", message)
        if (nickname != null) {
            json.addProperty("nickname", nickname)
        }

        socketCache!!.emit("clientMessage", Gson().toJson(json).toByteArray(Charsets.UTF_8))
    }

    /**
     * Reply message by message id
     * @param message message content
     * @param uuid message uuid to reply to
     * @param nickname user's nickname
     */
    fun replyMessage(message: String, uuid: String, nickname: String? = null) {
        connectCheck()

        val json = JsonObject()
        json.addProperty("message", message)
        json.addProperty("replyMessageUUID", uuid)
        if (nickname != null) {
            json.addProperty("nickname", nickname)
        }

        socketCache!!.emit("clientMessage", Gson().toJson(json).toByteArray(Charsets.UTF_8))
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
            val gson: Gson = GsonBuilder()
                .registerTypeAdapter(Timestamp::class.java, TimestampAdapter())
                .create()
            val message: CosmicChatMessage = gson.fromJson(json, CosmicChatMessage::class.java)
            handler(message)
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
