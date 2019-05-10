package com.lft.netty.test_2018_12_6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 测试出站的 编码
 * @author Ryze
 * @date 2018-12-06 10:23
 */
public class TestOutboud {
    @Test
    public void test1() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            //注意 写入 writeInt
            buffer.writeInt(-i);
        }
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        //写入
        assertTrue(channel.writeOutbound(buffer));
        assertTrue(channel.finish());
        //测试
        for (int i = 1; i < 10; i++) {
            assertEquals(i, (int) channel.readOutbound());
        }
        assertNull(channel.readOutbound());
    }
}
