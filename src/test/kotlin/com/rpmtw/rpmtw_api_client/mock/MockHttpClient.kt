package com.rpmtw.rpmtw_api_client.mock

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.requests.DefaultBody
import com.google.gson.Gson
import java.io.ByteArrayInputStream

class MockHttpClient {
    companion object {
        fun mockRequest(response: MockHttpResponse) {
            val httpClient = object : Client {
                override fun executeRequest(request: Request): Response {
                    return Response(
                        body = if (response.data != null) DefaultBody({
                            ByteArrayInputStream(
                                Gson().toJson(response.data).toByteArray()
                            )
                        }) else DefaultBody(),
                        statusCode = response.statusCode,
                        responseMessage = response.responseMessage,
                        url = request.url
                    )
                }
            }
            FuelManager.instance.client = httpClient
        }
    }
}