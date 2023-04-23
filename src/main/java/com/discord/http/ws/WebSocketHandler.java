package com.discord.http.ws;

import com.discord.http.JsonHandler;
import com.discord.util.Docs;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Docs(value = "https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/http/websocketx/client/WebSocketClientHandler.java", borrowed = true)
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {
    private Logger logger = LogManager.getLogger("Gateway/Handler");
    private final WebSocketClientHandshaker handshaker;
    private final List<JsonHandler> handlers;
    private ChannelPromise handshakeFuture;

    public ChannelPromise handshakeFuture() {
        return handshakeFuture;
    }

    public WebSocketHandler(WebSocketClientHandshaker handshaker, List<JsonHandler> handlers) {
        this.handshaker = handshaker;
        this.handlers = handlers;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        handshakeFuture = ctx.newPromise();
        logger.info("Handler has been added");
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.warn("Handshake in progress...");
        handshaker.handshake(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();

        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(channel, (FullHttpResponse) msg);
                logger.info("Successfully connected to WebSocket");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                logger.fatal("Failed to connect to WebSocket");
                handshakeFuture.setFailure(e);
            }
            return;
        }
        if (msg instanceof FullHttpResponse response) {
            logger.fatal("Unexpected fatal error: ", new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')'));
        }
        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame textFrame) {
            if (!handlers.isEmpty()) {
                handlers.forEach(h -> h.handle(new Gson().fromJson(textFrame.text(), JsonObject.class)));
            }
        } else if (frame instanceof CloseWebSocketFrame) {
            logger.warn("WebSocket requested closure of current handler");
            channel.close();
        }
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.warn("Client has disconnected");
    }
}
