package com.lft.netty.test_2018_12_6;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * 1.数据流 由一系列的帧组成 以\n 尾分隔符
 * 2.每个帧 由一系列的命令元素组成 以' '分隔
 * 3.一个帧 代表一个命令 定义为 一个命令+数目可变的参数
 * Cmd 将帧（命令）的内容存在ByteBuf 中，一个ByteBuf 用于名称 另一个用于参数
 * CmdDecoder 从被重写的decode() 方法获取一行字符串 并将它的内容构建一个Cmd 实例
 * CmdHandler 从上一步的Cmd对象进行一系列处理
 * @author Ryze
 * @date 2018-12-06 17:53
 */
public class MyDelimiterBasedFrameInitializer extends ChannelInitializer<Channel> {
    final byte SPACE = (byte) ' ';

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new CmdDecoder(6 * 1024))
            .addLast(new CmdHandler());
    }

    private class CmdDecoder extends LineBasedFrameDecoder {
        public CmdDecoder(int i) {
            super(i);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
            //1 解决
            ByteBuf decode = (ByteBuf) super.decode(ctx, buffer);
            if (decode == null) {
                return null;
            }
            //2.解决
            int i = decode.indexOf(decode.readerIndex(), decode.writerIndex(), SPACE);
            return new Cmd(decode.slice(decode.readerIndex(), i), decode.slice(i + 1, decode.writerIndex()));
        }
    }

    private class CmdHandler extends SimpleChannelInboundHandler<Cmd> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Cmd msg) throws Exception {
            // cmd 处理
        }
    }
    private class Cmd {
        private ByteBuf name;
        private ByteBuf args;

        public ByteBuf getName() {
            return name;
        }

        public ByteBuf getArgs() {
            return args;
        }

        public Cmd(ByteBuf name, ByteBuf args) {
            this.name = name;
            this.args = args;
        }
    }
}
