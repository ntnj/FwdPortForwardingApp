package com.elixsr.portforwarder.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;

/**
 * Created by Cathan on 14/07/2017.
 */

public class RuleListTargetJsonDeserializer implements JsonDeserializer<InetSocketAddress>
{
    @Override
    public InetSocketAddress deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException
    {

        JsonObject jsonObject = je.getAsJsonObject();

        if(jsonObject.has("hostname") && jsonObject.has("port")) {
            return new InetSocketAddress(jsonObject.get("hostname").getAsString(), jsonObject.get("port").getAsInt());
        } else {
            throw new JsonParseException("Target is missing host and port");
        }

    }
}