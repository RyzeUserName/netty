package com.lft.netty.test_2018_12_12.webSocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 初始化 pipeline
 * @author Ryze
 * @date 2018-12-12 17:17
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }


    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            //将byte 和 http(HttpRequest HttpContent LastHttpContent) 进行相互装换
            .addLast(new HttpServerCodec())
            //写入文件内容
            .addLast(new ChunkedWriteHandler())
            //将http 转为整个FullHttpRequest或者 FullHttpResponse
            .addLast(new HttpObjectAggregator(64 * 1024))
            .addLast(new HttpRequestHandler("/ws"))
            //按照 webSocket 规范处理webSocket升级握手 ping pong close
            .addLast(new WebSocketServerProtocolHandler("/ws"))
            .addLast(new TextWebSocketFrameHandler(group));
    }
}
