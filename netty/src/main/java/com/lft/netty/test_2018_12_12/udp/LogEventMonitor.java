package com.lft.netty.test_2018_12_12.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

/**
 * 描述
 * @author Ryze
 * @date 2018-12-12 23:18
 */
public class LogEventMonitor {
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;

    public static void main(String[] args) {
        LogEventMonitor monitor = new LogEventMonitor(new InetSocketAddress(9999));
        try {
            Channel bind = monitor.bind();
            System.out.println(" monitor running");
            bind.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            monitor.stop();
        }
    }

    public Channel bind() {
        return bootstrap.bind().syncUninterruptibly().channel();
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public LogEventMonitor(InetSocketAddress address) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
            .channel(NioDatagramChannel.class)
            .option(ChannelOption.SO_BROADCAST, true)
            .handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new LogEventDecoder())
                        .addLast(new LogEventHandler());
                }
            }).localAddress(address);
    }
}
