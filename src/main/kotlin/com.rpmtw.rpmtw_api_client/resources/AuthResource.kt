package com.rpmtw.rpmtw_api_client.resources

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.fuel.httpGet
import com.rpmtw.rpmtw_api_client.exceptions.ModelNotFoundException
import com.rpmtw.rpmtw_api_client.models.auth.User
import com.rpmtw.rpmtw_api_client.utilities.Utilities
import kotlinx.coroutines.runBlocking

class AuthResource(override val baseUrl: String, override val globalToken: String?) : BaseResource {
    suspend fun getUserByUUID(uuid: String, token: String? = null): User {
        return runBlocking {
            val url = "$baseUrl/auth/user/$uuid"
            val request: Request = url.httpGet()
            if (globalToken != null || token != null) {
                request.header("Authorization", "Bearer ${globalToken ?: token}")
            }

            request.awaitStringResult().fold(
                { return@fold Utilities.jsonDeserialize(it, User::class.java) },
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

            request.awaitStringResult().fold(
                { return@fold Utilities.jsonDeserialize(it, User::class.java) },
                {
                    if (it.response.statusCode == 404) {
                        throw ModelNotFoundException(User::class)
                    }

                    throw it
                })
        }
    }
}