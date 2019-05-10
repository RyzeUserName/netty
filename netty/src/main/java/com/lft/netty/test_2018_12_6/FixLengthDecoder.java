package com.lft.netty.test_2018_12_6;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 对 入站信息 解码 按指定长度读取.切分
 * @author Ryze
 * @date 2018-12-06 9:47
 */
public class FixLengthDecoder extends ByteToMessageDecoder {
    private final int length;

    public FixLengthDecoder(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("长度不能小于零");
        }
        this.length = length;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= length) {
            ByteBuf byteBuf = in.readBytes(length);
            out.add(byteBuf);
        }
    }
}
