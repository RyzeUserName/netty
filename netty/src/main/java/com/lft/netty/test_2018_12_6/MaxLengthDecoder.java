package com.lft.netty.test_2018_12_6;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * 对 入站信息 解码 按指定帧的最大长度
 * @author Ryze
 * @date 2018-12-06 9:47
 */
public class MaxLengthDecoder extends ByteToMessageDecoder {
    private final int length;

    public MaxLengthDecoder(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("长度不能小于零");
        }
        this.length = length;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        //超过指定帧的大小则丢弃，防止资源耗尽
        if (readableBytes > length) {
            in.clear();
            throw new TooLongFrameException();
        }
        ByteBuf byteBuf = in.readBytes(readableBytes);
        out.add(byteBuf);
    }
}
