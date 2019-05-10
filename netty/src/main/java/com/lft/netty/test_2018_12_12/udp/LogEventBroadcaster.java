package com.lft.netty.test_2018_12_12.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

/**
 * 广播员
 * @author Ryze
 * @date 2018-12-12 22:24
 */
public class LogEventBroadcaster {
    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;
    private final File file;

    public LogEventBroadcaster(InetSocketAddress address, File file) {
        this.eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
            .channel(NioDatagramChannel.class)
            .option(ChannelOption.SO_BROADCAST, true)
            .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public static void main(String[] args) {
        LogEventBroadcaster logEventBroadcaster =
            new LogEventBroadcaster(new InetSocketAddress("255.255.255.255", 9999),
                new File("F:\\study\\netty\\src\\main\\resources\\log.log"));
        try {
            logEventBroadcaster.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logEventBroadcaster.stop();
        }
    }

    public void start() throws Exception {
        Channel channel = bootstrap.bind(0).sync().channel();
        long pointer = 0L;
        for (; ; ) {
            //有新的日志生成 那么就写出去
            long length = file.length();
            if (length < pointer) {
                pointer = length;
            } else if (length > pointer) {
                RandomAccessFile r = new RandomAccessFile(file, "r");
                r.seek(pointer);
                String line;
                while ((line = r.readLine()) != null) {
                    channel.writeAndFlush(new LogEvent(file.getAbsolutePath(), line));
                }
                pointer = r.getFilePointer();
                r.close();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }

    public void stop() {
        eventLoopGroup.shutdownGracefully();
    }
}
