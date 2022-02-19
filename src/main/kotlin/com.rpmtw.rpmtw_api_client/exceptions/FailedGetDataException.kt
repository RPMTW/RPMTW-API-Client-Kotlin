package com.rpmtw.rpmtw_api_client.exceptions

import com.github.kittinunf.fuel.core.FuelError

class FailedGetDataException(private val error: FuelError) : Exception() {
    override val message: String
        get() = "Failed to get data from server.\nError: ${error.message} (${error.response.statusCode})"
}