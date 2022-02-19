package com.rpmtw.rpmtw_api_client.utilities

import com.rpmtw.rpmtw_api_client.RPMTWApiClient

class TestUtilities {
    companion object {
        fun setUp() {
            RPMTWApiClient.clearCache()
            RPMTWApiClient.init()
        }
    }
}