package com.lft.netty.test_2018_12_12.webSocket;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * 加密的
 * @author Ryze
 * @date 2018-12-12 19:00
 */
public class SecurityChatServerInitializer extends ChatServerInitializer {
    private final SslContext sslContext;

    public SecurityChatServerInitializer(ChannelGroup group, SslContext sslContext) {
        super(group);
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        super.initChannel(ch);
        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        sslEngine.setUseClientMode(false);
        ch.pipeline().addFirst(new SslHandler(sslEngine));
    }
}
