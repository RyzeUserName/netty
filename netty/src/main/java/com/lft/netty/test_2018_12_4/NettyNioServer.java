package com.lft.netty.test_2018_12_4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * netty 写的 Nio
 * @author Ryze
 * @date 2018-12-04 11:48
 */
public class NettyNioServer {

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        NioEventLoopGroup eventExecutors = null;
        try {
            ByteBuf byteBuf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("hello !", CharsetUtil.UTF_8));
             eventExecutors = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventExecutors)
                .channel(NioServerSocketChannel.class)
                .localAddress(9000)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ctx) throws Exception {
                        ctx.writeAndFlush(byteBuf.duplicate()).addListener(ChannelFutureListener.CLOSE);
                    }
                });
            ChannelFuture future = serverBootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                eventExecutors.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
