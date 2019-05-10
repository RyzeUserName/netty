package com.lft.netty.test_2018_12_3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * 服务端
 * @author Ryze
 * @date 2018-12-03 23:50
 */
public class EchoServer {
    public static void main(String[] args) {
        start();
    }

    static void start() {
        ChannelFuture sync = null;
        NioEventLoopGroup nioEventLoopGroup = null;
        try {
            final EchoServerHandler echoServerHandler = new EchoServerHandler();
            nioEventLoopGroup = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(nioEventLoopGroup).channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(9000))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(echoServerHandler);
                    }
                });
            sync = serverBootstrap.bind().sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                nioEventLoopGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
