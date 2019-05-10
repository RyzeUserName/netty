package com.lft.netty.test_2018_12_5;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 *  使用  ChannelOption
 * @author Ryze
 * @date 2018-12-05 22:49
 */
public class ChannelOptionTest {

    public static void main(String[] args) {
        //线程不安全的
        //final AttributeKey<Integer> id = AttributeKey.newInstance("ID");
        //线程安全的
        final AttributeKey<Integer> id = AttributeKey.valueOf("ID");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                @Override
                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("注册成功");
                    Integer i = ctx.channel().attr(id).get();
                    System.out.println("id 是" + i);
                }
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                    System.out.println("收到 数据");
                }
            });
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000);
        bootstrap.attr(id,123456);
        ChannelFuture connect = bootstrap.connect(new InetSocketAddress("", 9000));
        connect.syncUninterruptibly();

    }
}
