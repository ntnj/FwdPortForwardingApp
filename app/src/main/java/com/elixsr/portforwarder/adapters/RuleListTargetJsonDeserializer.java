package com.elixsr.portforwarder.adapters;

import com.elixsr.portforwarder.exceptions.RuleValidationException;
import com.elixsr.portforwarder.util.IpAddressValidator;
import com.elixsr.portforwarder.validators.RuleModelValidator;
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
        IpAddressValidator ipAddressValidator = new IpAddressValidator();

        try {
            if(jsonObject.has("hostname") && jsonObject.has("port")
                    && ipAddressValidator.validate(jsonObject.get("hostname").getAsString())
                    && RuleModelValidator.validateRuleTargetPort(jsonObject.get("port").getAsInt())) {
                return new InetSocketAddress(jsonObject.get("hostname").getAsString(), jsonObject.get("port").getAsInt());
            } else {
                throw new JsonParseException("Target is missing host and port");
            }
        } catch (RuleValidationException e) {
            e.printStackTrace();
            throw new JsonParseException("Port outside range");
        }

    }
}