package com.lft.netty.test_2018_12_6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 测试入站
 * @author Ryze
 * @date 2018-12-06 9:44
 */
public class TestInbound {

    @Test
    public void testInBound() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf duplicate = buffer.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixLengthDecoder(3));
        //写入 9个 字节
        assertTrue(channel.writeInbound(duplicate.retain()));
        assertTrue(channel.finish());

        //第一次读
        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //第二次读
        read = (ByteBuf) channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();


        //第三次读
        read = (ByteBuf) channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //最后
        assertNull(channel.readInbound());
        buffer.release();
    }
    @Test
    public void testInBound2() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf duplicate = buffer.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixLengthDecoder(3));
        //写入分两次写入 两字节 不会 =true
        assertFalse(channel.writeInbound(duplicate.readBytes(2)));
        assertTrue(channel.writeInbound(duplicate.readBytes(7)));
        assertTrue(channel.finish());

        //第一次读
        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //第二次读
        read = (ByteBuf) channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();


        //第三次读
        read = (ByteBuf) channel.readInbound();
        assertEquals(buffer.readSlice(3), read);
        read.release();

        //最后
        assertNull(channel.readInbound());
        buffer.release();
    }
}
