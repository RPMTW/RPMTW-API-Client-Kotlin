package com.rpmtw.rpmtw_api_client.mock

data class MockHttpResponse(
    var data: Any?, val statusCode: Int = 200, val responseMessage: String = "OK"
)