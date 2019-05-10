package com.lft.netty.test_2018_12_6;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * WebSocketServer 服务端
 * @author Ryze
 * @date 2018-12-06 16:54
 */
public class WebSocketServerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            //编码
            .addLast(new HttpServerCodec())
            //聚合数据
            .addLast(new HttpObjectAggregator(65535))
            //握手的处理端点
            .addLast(new WebSocketServerProtocolHandler("/websocket"))
            //数据帧是文本数据的处理
            .addLast(new TextFrameHandler())
            //数据帧是二进制的处理
            .addLast(new BinaryFrameHandler())
            //数据帧 属于上一个 TextWebSocketFrame或者 BinaryWebSocketFrame的数据 处理
            .addLast(new ContinuationWebSocketFrameHandler());

    }

    /**
     * 数据帧是文本数据的处理
     */
    private class TextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
            //处理
        }
    }

    /**
     * 数据帧是二进制的处理
     */
    private class BinaryFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
            //处理
        }
    }


    /**
     * 数据帧 属于上一个 TextWebSocketFrame或者 BinaryWebSocketFrame的数据
     */
    private class ContinuationWebSocketFrameHandler extends SimpleChannelInboundHandler<ContinuationWebSocketFrame> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ContinuationWebSocketFrame msg) throws Exception {
            //处理
        }
    }
}
