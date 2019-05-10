package com.lft.netty.test_2018_12_6;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * 添加 ssl的支持
 * @author Ryze
 * @date 2018-12-06 15:48
 */
public class SslChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext sslContext;
    private final boolean startTls;

    /**
     * @param sslContext 要的 sslContext
     * @param startTls 为true 第一个写入的不会被加密  客户端应该设为true
     */
    public SslChannelInitializer(SslContext sslContext, boolean startTls) {
        this.sslContext = sslContext;
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        ch.pipeline().addFirst("ssl", new SslHandler(sslEngine, startTls));
    }
}
