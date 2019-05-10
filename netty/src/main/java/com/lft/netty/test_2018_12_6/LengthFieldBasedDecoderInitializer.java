package com.lft.netty.test_2018_12_6;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 根据头字节写的长度 提取帧
 * @author Ryze
 * @date 2018-12-06 18:30
 */
public class LengthFieldBasedDecoderInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            //前八字节表示长度
            .addLast(new LengthFieldBasedFrameDecoder(64* 1024, 0, 8))
            //提取帧 之后的处理
            .addLast(new FramHandler());

    }

    private class FramHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            //处理
        }
    }
}
