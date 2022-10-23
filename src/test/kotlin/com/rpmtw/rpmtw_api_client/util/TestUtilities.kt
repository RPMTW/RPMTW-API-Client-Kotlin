package com.rpmtw.rpmtw_api_client.util

import com.rpmtw.rpmtw_api_client.RPMTWApiClient

class TestUtilities {
    companion object {
        fun setUp() {
            RPMTWApiClient.clearCache()
            RPMTWApiClient.init()
        }
    }
}