package com.rpmtw.rpmtw_api_client.utilities

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object Utilities {
    @JvmStatic
    fun <T> jsonDeserialize(json: String, className: Class<T>, gson: Gson = Gson()): T {
        val jsonObject: JsonObject = JsonParser.parseString(json).asJsonObject
        jsonObject["data"].let {
            return gson.fromJson(it, className)
        }
    }
}