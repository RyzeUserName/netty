package com.lft.netty.test_2018_12_6;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * 支持 http
 * @author Ryze
 * @date 2018-12-06 16:13
 */
public class HttpHandler extends ChannelInitializer<Channel> {
    private final boolean client;

    public HttpHandler(boolean client) {
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //如果是客户端 请求加密 回应解析
        if (client) {
            pipeline.addLast("decoder", new HttpResponseDecoder())
                .addLast("encoder", new HttpRequestEncoder());
        } else {
            //如果是服务端 请求解析 回应加密
            pipeline.addLast("decoder", new HttpRequestDecoder())
                .addLast("encoder", new HttpResponseEncoder());
        }
    }
}
