package com.rpmtw.rpmtw_api_client.resources

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.fuel.httpGet
import com.rpmtw.rpmtw_api_client.exceptions.FailedGetDataException
import com.rpmtw.rpmtw_api_client.exceptions.ModelNotFoundException
import com.rpmtw.rpmtw_api_client.models.storage.Storage
import com.rpmtw.rpmtw_api_client.util.Util
import kotlinx.coroutines.runBlocking

class StorageResource(override val apiBaseUrl: String, override val globalToken: String?) : BaseResource {
    /**
     * Get storage info by uuid.
     * @param uuid uuid of the storage
     */
    suspend fun getStorage(uuid: String): Storage {
        return runBlocking {
            val url = "$apiBaseUrl/storage/$uuid"
            val request: Request = url.httpGet()

            request.awaitStringResult().fold({ return@fold Util.jsonDeserialize(it, Storage::class.java) }, {
                if (it.response.statusCode == 404) {
                    throw ModelNotFoundException(Storage::class)
                }

                throw FailedGetDataException(it)
            })
        }
    }
}