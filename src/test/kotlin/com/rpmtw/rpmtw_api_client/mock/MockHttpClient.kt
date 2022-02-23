package com.rpmtw.rpmtw_api_client.mock

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.requests.DefaultBody
import com.github.kittinunf.fuel.toolbox.HttpClient
import com.google.gson.Gson
import com.google.gson.JsonElement
import java.io.ByteArrayInputStream

class MockHttpClient {
    companion object {
        fun mockRequest(response: MockHttpResponse) {
            val httpClient = object : Client {
                @Suppress("LiftReturnOrAssignment")
                override fun executeRequest(request: Request): Response {
                    val body: Body
                    val data: Any? = response.data
                    if (data != null) {
                        if (data is JsonElement) {
                            body = DefaultBody({ ByteArrayInputStream(Gson().toJson(data).toByteArray()) })
                        } else {
                            body = DefaultBody({
                                ByteArrayInputStream(
                                    "{\"data\":${Gson().toJson(response.data)}}".toByteArray()
                                )
                            })
                        }
                    } else {
                        body = DefaultBody()
                    }
                    FuelManager.instance.client =
                        HttpClient(hook = FuelManager.instance.hook, proxy = FuelManager.instance.proxy)

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