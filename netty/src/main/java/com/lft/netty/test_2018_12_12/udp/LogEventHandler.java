package com.lft.netty.test_2018_12_12.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 日志聚合
 * @author Ryze
 * @date 2018-12-12 23:11
 */
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent logEvent) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(logEvent.getReceived())
            .append(" [ ")
            .append(logEvent.getInetSocketAddress().toString())
            .append(" ] [ ")
            .append(logEvent.getLogfile())
            .append(" ] : ")
            .append(logEvent.getMsg());
        System.out.println(sb.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
