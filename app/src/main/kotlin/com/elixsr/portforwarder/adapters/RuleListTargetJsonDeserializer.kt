package com.elixsr.portforwarder.adapters

import com.elixsr.portforwarder.exceptions.RuleValidationException
import com.elixsr.portforwarder.util.IpAddressValidator
import com.elixsr.portforwarder.validators.RuleModelValidator
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.net.InetSocketAddress

/**
 * Created by Cathan on 14/07/2017.
 */
class RuleListTargetJsonDeserializer : JsonDeserializer<InetSocketAddress> {
    @Throws(JsonParseException::class)
    override fun deserialize(je: JsonElement, type: Type, jdc: JsonDeserializationContext): InetSocketAddress {
        val jsonObject = je.asJsonObject
        val ipAddressValidator = IpAddressValidator()
        return try {
            if (jsonObject.has("hostname") && jsonObject.has("port")
                    && ipAddressValidator.validate(jsonObject["hostname"].asString)
                    && RuleModelValidator.validateRuleTargetPort(jsonObject["port"].asInt)) {
                InetSocketAddress(jsonObject["hostname"].asString, jsonObject["port"].asInt)
            } else {
                throw JsonParseException("Target is missing host and port")
            }
        } catch (e: RuleValidationException) {
            e.printStackTrace()
            throw JsonParseException("Port outside range")
        }
    }
}