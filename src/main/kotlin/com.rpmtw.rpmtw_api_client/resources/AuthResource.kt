package com.rpmtw.rpmtw_api_client.resources

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.rpmtw.rpmtw_api_client.exceptions.FailedGetDataException
import com.rpmtw.rpmtw_api_client.exceptions.ModelNotFoundException
import com.rpmtw.rpmtw_api_client.models.auth.CreateUserResult
import com.rpmtw.rpmtw_api_client.models.auth.User
import com.rpmtw.rpmtw_api_client.util.Util
import kotlinx.coroutines.runBlocking

class AuthResource(override val apiBaseUrl: String, override val globalToken: String?) : BaseResource {
    /**
     * Get user info by uuid.
     * @param uuid user's uuid (When uuid is **me**, use token to get the user info of the token)
     * @param token (optional)
     * @see getUserByEmail
     */
    suspend fun getUserByUUID(uuid: String, token: String? = null): User {
        return runBlocking {
            val url = "$apiBaseUrl/auth/user/$uuid"
            var request: Request = url.httpGet()
            if (globalToken != null || token != null) {
                request = request.header("Authorization", "Bearer ${globalToken ?: token}")
            }

            request.awaitStringResult().fold({ return@fold Util.jsonDeserialize(it, User::class.java) }, {
                if (it.response.statusCode == 404) {
                    throw ModelNotFoundException(User::class)
                }

                throw FailedGetDataException(it)
            })
        }
    }

    /**
     * Get user info by email.
     * @param email user's email
     * @see getUserByUUID
     */
    suspend fun getUserByEmail(email: String): User {
        return runBlocking {
            val url = "$apiBaseUrl/auth/user/get-by-email/$email"
            val request: Request = url.httpGet()

            request.awaitStringResult().fold({ return@fold Util.jsonDeserialize(it, User::class.java) }, {
                if (it.response.statusCode == 404) {
                    throw ModelNotFoundException(User::class)
                }

                throw FailedGetDataException(it)
            })
        }
    }

    /**
     * Create a user account, if successful, will return token and user information.
     * @param username user's username
     * @param password user's password
     * @param email user's email address
     * @param avatarStorageUUID user's avatar storage uuid (optional)
     */
    suspend fun createUser(
        username: String, password: String, email: String, avatarStorageUUID: String? = null
    ): CreateUserResult {
        return runBlocking {
            val url = "$apiBaseUrl/auth/user/create"

            val gson = Gson()
            val json = JsonObject()
            json.addProperty("username", username)
            json.addProperty("password", password)
            json.addProperty("email", email)
            if (avatarStorageUUID != null) {
                json.addProperty("avatarStorageUUID", avatarStorageUUID)
            }

            val request: Request = url.httpPost().jsonBody(gson.toJson(json))

            request.awaitStringResult().fold({
                val user: User = Util.jsonDeserialize(it, User::class.java)
                val jsonObject: JsonObject = JsonParser.parseString(it).asJsonObject

                return@fold CreateUserResult(jsonObject["data"].asJsonObject["token"].asString, user)
            }, {
                throw FailedGetDataException(it)
            })
        }
    }
}