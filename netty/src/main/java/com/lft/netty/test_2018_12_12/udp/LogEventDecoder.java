package com.lft.netty.test_2018_12_12.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * 日志 解码
 * @author Ryze
 * @date 2018-12-12 23:03
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {
        ByteBuf data = datagramPacket.content();
        int index = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR);
        String file = data.slice(0, index).toString(CharsetUtil.UTF_8);
        String msg = data.slice(index + 1, data.readableBytes()).toString(CharsetUtil.UTF_8);
        LogEvent logEvent = new LogEvent(datagramPacket.sender(), file, msg, System.currentTimeMillis());
        out.add(logEvent);
    }
}
