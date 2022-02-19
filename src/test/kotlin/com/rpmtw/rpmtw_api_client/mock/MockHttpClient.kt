package com.rpmtw.rpmtw_api_client.mock

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.requests.DefaultBody
import com.google.gson.Gson
import java.io.ByteArrayInputStream

class MockHttpClient {
    companion object {
        fun mockRequest(response: MockHttpResponse) {
            val httpClient = object : Client {
                override fun executeRequest(request: Request): Response {
                    val body: Body = if (response.data != null) {
                        DefaultBody({
                            ByteArrayInputStream(
                                "{\"data\":${Gson().toJson(response.data)}}".toByteArray()
                            )
                        })
                    } else {
                        DefaultBody()
                    }

                    return Response(
                        body = body,
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