package com.elixsr.portforwarder.adapters

import com.elixsr.portforwarder.models.RuleModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.net.InetSocketAddress

/**
 * Created by Cathan on 23/07/2017.
 */
class RuleListJsonValidator : JsonDeserializer<RuleModel> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): RuleModel {
        val gson = GsonBuilder()
                .registerTypeAdapter(InetSocketAddress::class.java, RuleListTargetJsonDeserializer())
                .create()
        val jsonObject = json.asJsonObject
        return if (jsonObject.has("fromInterfaceName") &&
                jsonObject.has("fromPort") &&
                jsonObject.has("isEnabled") &&
                jsonObject.has("isTcp") &&
                jsonObject.has("isUdp") &&
                jsonObject.has("name") &&
                jsonObject.has("target")) {
            gson.fromJson(jsonObject, RuleModel::class.java)
        } else {
            throw JsonParseException("Rule is invalid.")
        }
    }

    companion object {
        private const val TAG = "RuleListJsonValidator"
    }
}