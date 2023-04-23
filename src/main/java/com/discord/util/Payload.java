package com.discord.util;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public final class Payload {
    private static final Payload globalPayload = new Payload();
    private static boolean shouldClose;

    private List<String> data;
    private Payload() {
        data = new ArrayList<>();
    }

    public static void sendString(String s) {
        globalPayload.data.add(s);
    }
    public static void sendJson(JsonObject object) {
        globalPayload.data.add(object.toString());
    }
    public static List<String> requestFlush() {
        List<String> data = globalPayload.data;
        globalPayload.data.clear();
        return data;
    }
    public static boolean isEmpty() {
        return globalPayload.data.isEmpty();
    }
    public static boolean shouldClose() {
        return shouldClose;
    }
    public static void setShouldClose(boolean close) {
        shouldClose = close;
    }
}
