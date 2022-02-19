package com.rpmtw.rpmtw_api_client.exceptions

class ClientUninitializedException : Exception() {
    override val message: String
        get() = "Client is not initialized, please call RPMTWApiClient.init() first"
}