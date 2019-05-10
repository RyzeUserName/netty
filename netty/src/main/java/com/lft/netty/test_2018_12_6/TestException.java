package com.lft.netty.test_2018_12_6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 测试 异常处理
 * @author Ryze
 * @date 2018-12-06 10:41
 */
public class TestException {

    @Test
    public void test1() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf duplicate = buffer.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new MaxLengthDecoder(3));
        //写入两字节产生一个帧
        assertTrue(channel.writeInbound(duplicate.readBytes(2)));
        try {
            //写入4字节产生一个帧
            assertFalse(channel.writeInbound(duplicate.readBytes(4)));
            Assert.fail();
        } catch (TooLongFrameException e) {
            //异常处理
        }
        //写入剩余的字节
        assertTrue(channel.writeInbound(duplicate.readBytes(3)));
        assertTrue(channel.finish());

        //读取写入的信息
        ByteBuf readInbound = channel.readInbound();
        //验证前两字节
        assertEquals(buffer.readSlice(2), readInbound);
        readInbound.release();

        //验证后 3字节
        readInbound = channel.readInbound();
        assertEquals(buffer.skipBytes(4).readSlice(3), readInbound);
        readInbound.release();
        buffer.release();
    }
}
