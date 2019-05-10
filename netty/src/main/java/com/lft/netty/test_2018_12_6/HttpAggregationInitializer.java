package com.lft.netty.test_2018_12_6;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * http 消息聚合
 * @author Ryze
 * @date 2018-12-06 16:22
 */
public class HttpAggregationInitializer extends ChannelInitializer<Channel> {
    private final boolean isClient;

    public HttpAggregationInitializer(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //如果是客户端
        if (isClient) {
            pipeline.addLast("codec", new HttpClientCodec());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
        }
        //将最大消息为 512K的 HttpObjectAggregator 添加到 pipeline
        //注意 ： HttpObjectAggregator 必须放在编解码 之后
        pipeline.addLast("aggregation", new HttpObjectAggregator(512 * 1024));
    }
}
