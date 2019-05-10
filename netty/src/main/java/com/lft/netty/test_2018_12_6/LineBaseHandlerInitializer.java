package com.lft.netty.test_2018_12_6;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * 处理 行尾是  \n 或者\r\n的分隔符
 * @author Ryze
 * @date 2018-12-06 17:45
 */
public class LineBaseHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new LineBasedFrameDecoder(64 * 1024))
            .addLast(new FrameHandler());

    }

    private static class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            //在这里 可以接收 完整的帧
        }
    }
}
