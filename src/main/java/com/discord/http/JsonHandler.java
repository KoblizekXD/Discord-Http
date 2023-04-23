package com.discord.http;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface JsonHandler {
    void handle(JsonObject object);
}
