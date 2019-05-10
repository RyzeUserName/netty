package com.lft.netty.test_2018_12_5;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * 客户端 无协议的
 * @author Ryze
 * @date 2018-12-05 21:13
 */
public class BoostrapClient {
    public static void main(String[] args) {
        start();
    }

    private static void start() {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture connect = bootstrap.group(eventExecutors)
            .channel(NioSocketChannel.class)
            .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

                }
            }).connect(new InetSocketAddress("ip", 80));
        connect.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                System.out.println("成功连接");
            } else {
                System.out.println("失败连接");
                future.cause().printStackTrace();
            }
        });
    }
}
