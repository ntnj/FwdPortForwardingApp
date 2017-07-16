package com.elixsr.portforwarder.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;

/**
 * Created by Cathan on 14/07/2017.
 */

public class RuleListJsonSerializer implements JsonSerializer<InetSocketAddress> {

    @Override
    public JsonElement serialize(InetSocketAddress src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject targetObject = new JsonObject();
        targetObject.addProperty("hostname", src.getAddress().getHostAddress());
        targetObject.addProperty("port", src.getPort());
        return targetObject;
    }
}