package com.rpmtw.rpmtw_api_client.exceptions

import kotlin.reflect.KClass


class ModelNotFoundException(private val model: KClass<*>) : Exception() {
    override val message: String
        get() = "${model.simpleName} not found"
}