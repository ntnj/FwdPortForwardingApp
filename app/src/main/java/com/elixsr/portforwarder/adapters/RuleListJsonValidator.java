package com.elixsr.portforwarder.adapters;

import android.util.Log;

import com.elixsr.portforwarder.models.RuleModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;

/**
 * Created by Cathan on 23/07/2017.
 */

public class RuleListJsonValidator implements JsonDeserializer<RuleModel> {

    private static final String TAG = "RuleListJsonValidator";

    @Override
    public RuleModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(InetSocketAddress.class, new RuleListTargetJsonDeserializer())
                .create();

        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("fromInterfaceName") &&
                jsonObject.has("fromPort") &&
                jsonObject.has("isEnabled") &&
                jsonObject.has("isTcp") &&
                jsonObject.has("isUdp") &&
                jsonObject.has("name") &&
                jsonObject.has("target")) {
            return gson.fromJson(jsonObject, RuleModel.class);
        } else {
            throw new JsonParseException("Rule is invalid.");
        }

    }
}
