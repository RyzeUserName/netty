package com.lft.netty.test_2018_12_5;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * 从channel 引导 客户端
 * @author Ryze
 * @date 2018-12-05 22:22
 */
@SuppressWarnings("all")
public class BoostrapServerAndSonClient {
    public static void main(String[] args) {
        start();
    }

    private static void start() {
        ServerBootstrap server = new ServerBootstrap();
        ChannelFuture future = server.group(new NioEventLoopGroup(), new NioEventLoopGroup())
            .channel(NioServerSocketChannel.class)
            .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                ChannelFuture channelFuture;

                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    Bootstrap bootstrap = new Bootstrap();
                    channelFuture = bootstrap.group(ctx.channel().eventLoop())
                        .channel(NioSocketChannel.class)
                        .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                System.out.println("收到数据");
                            }
                        }).connect(new InetSocketAddress("", 9000));

                }

                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                    if (channelFuture.isDone()) {
                        //处理
                        System.out.println("收到数据");
                    }
                }
            })
            .bind(new InetSocketAddress(9000));
        future.addListener((ChannelFutureListener) future1 -> {
            if (future.isSuccess()) {
                System.out.println("服务器绑定成功");
            } else {
                System.out.println("服务器绑定失败");
                future.cause().printStackTrace();
            }
        });

    }
}
