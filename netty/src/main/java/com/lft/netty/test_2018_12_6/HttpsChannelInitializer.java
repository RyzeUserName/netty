package com.lft.netty.test_2018_12_6;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * 添加 https的支持
 * @author Ryze
 * @date 2018-12-06 15:48
 */
public class HttpsChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext sslContext;
    private final boolean isClient;

    /**
     * @param sslContext 要的 sslContext
     * @param isClient 是不是客户端
     */
    public HttpsChannelInitializer(SslContext sslContext, boolean isClient) {
        this.sslContext = sslContext;
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        ChannelPipeline pipeline = ch.pipeline().addFirst("ssl", new SslHandler(sslEngine));
        //如果是客户端
        if (isClient) {
            pipeline.addLast("codec", new HttpClientCodec());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
        }
    }
}
