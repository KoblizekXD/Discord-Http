package com.discord.http;

import com.discord.http.ws.HandshakerV13;
import com.discord.http.ws.WebSocketHandler;
import com.discord.util.Payload;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static io.netty.handler.codec.http.websocketx.WebSocketVersion.V13;

public final class Gateway {
    public Gateway() {
        handlers = new ArrayList<>();
    }
    public void run() {
        URI uri = URI.create("wss://gateway.discord.gg/?v=10&encoding=json");

        final SslContext ssl = createSsl();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            final WebSocketHandler handler =
                    new WebSocketHandler(
                            newHandshaker(
                                    uri,null, true, new DefaultHttpHeaders()), handlers);
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            if (ssl != null) {
                                p.addLast(ssl.newHandler(ch.alloc(), "gateway.discord.gg", 443));
                            }
                            p.addLast(
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(8192),
                                    WebSocketClientCompressionHandler.INSTANCE,
                                    handler);
                        }
                    });

            Channel channel = b.connect(uri.getHost(), 443).sync().channel();
            handler.handshakeFuture().sync();
            while (true) {
                if (!Payload.isEmpty()) {
                    for (String requestFlush : Payload.requestFlush()) {
                        WebSocketFrame frame = new TextWebSocketFrame(requestFlush);
                        channel.writeAndFlush(frame);
                    }
                } else if (Payload.shouldClose()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            logger.error("Synchronization has been interrupted, disconnecting...");
            Thread.currentThread().interrupt();
        } finally {
            group.shutdownGracefully();
        }
    }
    private static Logger logger = LogManager.getLogger("Gateway");
    private static WebSocketClientHandshaker newHandshaker(URI uri, String subProtocol, boolean allowExt, HttpHeaders headers) {
        return new HandshakerV13(
                uri, V13, subProtocol, allowExt, headers, 65536,true,
                false,-1
        );
    }
    private SslContext createSsl() {
        try {
            return SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } catch (SSLException e) {
            logger.warn("Failed to secure your connection with SSL");
            return null;
        }
    }
    private List<JsonHandler> handlers;
    public Gateway addHandler(JsonHandler handler) {
        handlers.add(handler);
        return this;
    }
}
