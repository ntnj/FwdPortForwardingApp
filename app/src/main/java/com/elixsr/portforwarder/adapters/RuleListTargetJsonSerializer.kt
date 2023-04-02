package com.elixsr.portforwarder.adapters

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.net.InetSocketAddress

/**
 * Created by Cathan on 14/07/2017.
 */
class RuleListTargetJsonSerializer : JsonSerializer<InetSocketAddress> {
    override fun serialize(src: InetSocketAddress, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val targetObject = JsonObject()
        targetObject.addProperty("hostname", src.address.hostAddress)
        targetObject.addProperty("port", src.port)
        return targetObject
    }
}