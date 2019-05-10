package com.lft.netty.test_2018_12_6;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 绝对值 编码器
 * @author Ryze
 * @date 2018-12-06 10:19
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        //Integer 4字节
        while (msg.readableBytes() >= 4) {
            int abs = Math.abs(msg.readInt());
            out.add(abs);
        }
    }
}
