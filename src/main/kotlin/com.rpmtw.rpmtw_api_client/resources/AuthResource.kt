package com.rpmtw.rpmtw_api_client.resources

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.coroutines.awaitObjectResult
import com.github.kittinunf.fuel.gson.gsonDeserializer
import com.github.kittinunf.fuel.httpGet
import com.rpmtw.rpmtw_api_client.exceptions.ModelNotFoundException
import com.rpmtw.rpmtw_api_client.models.auth.User
import kotlinx.coroutines.runBlocking

class AuthResource(override val baseUrl: String, override val globalToken: String?) : BaseResource {
    suspend fun getUserByUUID(uuid: String, token: String? = null): User {
        return runBlocking {
            val url = "$baseUrl/auth/user/$uuid"
            val request: Request = url.httpGet()
            if (globalToken != null || token != null) {
                request.header("Authorization", "Bearer ${globalToken ?: token}")
            }

            request.awaitObjectResult(deserializable = gsonDeserializer<User>()).fold(
                { return@fold it },
                {
                    if (it.response.statusCode == 404) {
                        throw ModelNotFoundException(User::class)
                    }

                    throw it
                })
        }
    }

    suspend fun getUserByEmail(email: String): User {
        return runBlocking {
            val url = "$baseUrl/auth/user/get-by-email/$email"
            val request: Request = url.httpGet()

            request.awaitObjectResult(deserializable = gsonDeserializer<User>()).fold(
                { return@fold it },
                {
                    if (it.response.statusCode == 404) {
                        throw ModelNotFoundException(User::class)
                    }

                    throw it
                })
        }
    }
}