package com.lft.netty.test_2018_12_6.testbyte;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 测试 写
 * @author Ryze
 * @date 2018-12-06 21:53
 */
public class Test1 {
    ByteBuf buffer = Unpooled.buffer();

    @Test
    public void test1() {

        final byte[] bytes = "##".getBytes(CharsetUtil.UTF_8);
        //两字节
        buffer.writeBytes(bytes);
        //两字节
        buffer.writeByte(1);
        buffer.writeByte(2);
        //两字节
        buffer.writeByte(3);
        buffer.writeByte(4);
        //17字节
        String s = "12345678901234567";
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String s1 = String.valueOf(chars[i]);
            buffer.writeBytes(s1.getBytes());
        }
        //2字节
        buffer.writeByte(0);
        buffer.writeByte(0);
        //1字节
        buffer.writeByte(27);
        buffer.writeBytes(bytes);
        ByteBuf duplicate = buffer.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ByteBuf delimiter = Unpooled.copiedBuffer(bytes);
                ch.pipeline()
                    //前两字节截取
                    .addLast(new DelimiterBasedFrameDecoder(64 * 1024, delimiter))
                    .addLast(new MyDeconderHandler());
            }
        });
        //写入
        channel.writeInbound(duplicate.retain());
        channel.finish();
    }

    private class MyDeconderHandler extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            assertEquals((int) in.readByte(), 1);
            assertEquals((int) in.readByte(), 2);
            assertEquals((int) in.readByte(), 3);
            assertEquals((int) in.readByte(), 4);
            byte[] bytes = new byte[17];
            in.readBytes(bytes);
            String s = new String(bytes);
            assertEquals(s, "12345678901234567");
            assertEquals((int) in.readByte(), 0);
            assertEquals((int) in.readByte(), 0);
            assertEquals((int) in.readByte(), 27);
        }
    }
}
