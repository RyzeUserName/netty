package com.lft.netty.test_2018_12_12.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 日志编码器
 * @author Ryze
 * @date 2018-12-12 22:13
 */
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {
    private final InetSocketAddress inetSocketAddress;

    public LogEventEncoder(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogEvent logEvent, List<Object> out) throws Exception {
        byte[] bytes = logEvent.getLogfile().getBytes(CharsetUtil.UTF_8);
        byte[] bytes1 = logEvent.getMsg().getBytes(CharsetUtil.UTF_8);
        ByteBuf buffer = ctx.alloc().buffer(bytes.length + bytes1.length + 1);
        buffer.writeBytes(bytes);
        buffer.writeByte(LogEvent.SEPARATOR);
        buffer.writeBytes(bytes1);
        out.add(new DatagramPacket(buffer, inetSocketAddress));
    }
}
