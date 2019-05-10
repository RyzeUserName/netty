package com.lft.netty.test_2018_12_12.webSocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

/**
 * 服务端 访问http://localhost:9999
 * @author Ryze
 * @date 2018-12-12 17:49
 */
@SuppressWarnings("all")
public class ChatServer {
    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private Channel channel;

    public static void main(String[] args) throws Exception{
        ChatServer chatServer = new ChatServer();
        ChannelFuture start = chatServer.start(new InetSocketAddress(Integer.valueOf(9999)));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> chatServer.destroy()));
        start.channel().closeFuture().syncUninterruptibly();

    }
    public ChatServerInitializer createInitializer(ChannelGroup channelGroup ){
       return new ChatServerInitializer(channelGroup);
    }
    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture future = serverBootstrap
            .group(eventLoopGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(createInitializer(channelGroup))
            .bind(address);
        future.syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        channelGroup.close();
        eventLoopGroup.shutdownGracefully();
    }

}
