package com.discord.http.ws;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker13;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.URI;

import static io.netty.handler.codec.http.HttpHeaderNames.ORIGIN;

public class HandshakerV13 extends WebSocketClientHandshaker13 {

    public HandshakerV13(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength) {
        super(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength);
    }

    public HandshakerV13(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch) {
        super(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch);
    }

    public HandshakerV13(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis) {
        super(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, forceCloseTimeoutMillis);
    }

    @Override
    protected FullHttpRequest newHandshakeRequest() {
        FullHttpRequest request = super.newHandshakeRequest();
        request.headers().remove(ORIGIN);
        return request;
    }
}
