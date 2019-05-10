package com.lft.netty.test_2018_12_10;

import com.google.common.collect.Lists;
import com.lft.netty.test_2018_12_3.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("all")
public class Server {
    @Test
    public void test() {
        start();
    }

    ByteBuf buffer = Unpooled.buffer();
    byte[] bytes;

    {
        try {
            bytes = getBytes(35);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        ChannelFuture sync = null;
        NioEventLoopGroup boss = null;
        NioEventLoopGroup work = null;
        try {
            final EchoServerHandler echoServerHandler = new EchoServerHandler();
            boss = new NioEventLoopGroup();
            work = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, work).channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(9000))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    ByteBuf delimiter = Unpooled.copiedBuffer(bytes, bytes);

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                            //截取
                            .addLast(new DelimiterBasedFrameDecoder(64 * 1024, delimiter))
                            //异或校验
                            .addLast(new MyDeconderHandler())
                            //取值
                            .addLast(new MyDeconderHandler2())
                            //写出
                            .addLast(new MyEncoderHandler());
                    }
                });
            sync = serverBootstrap.bind().sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                boss.shutdownGracefully().sync();
                work.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyDeconderHandler extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            String s = new String(bytes);
            //bbc校验
            List<Integer> integers = bytesToHexString(s);
            if (integers != null) {
                out.add(integers);
            } else {
                in.release();
                ctx.channel().close();
            }
        }
    }

    Integer one = 1;
    Integer two = 254;
    Integer four = 4;

    private class MyDeconderHandler2 extends MessageToMessageDecoder<List<Integer>> {
        @Override
        protected void decode(ChannelHandlerContext ctx, List<Integer> msg, List<Object> out) throws Exception {
            int size = msg.size();
            if (size < 24) {
                ctx.channel().close();
            } else {
                //0x01 服务端口请求
                Iterator<Integer> iterator = msg.iterator();
                Integer next = iterator.next();
                if (next.equals(one)) {
                    System.out.println("服务端口请求");
                }
                //0xFE
                next = iterator.next();
                if (next.equals(two)) {
                    System.out.println("主动发起请求");
                }
                //0x01
                next = iterator.next();
                if (next.equals(one)) {
                    System.out.println("主版本号");
                }
                //0x04
                next = iterator.next();
                if (next.equals(four)) {
                    System.out.println("次版本号");
                }
                ctx.writeAndFlush("192.168.0.68:9999");
            }
        }
    }

    private class MyEncoderHandler extends MessageToMessageEncoder<String> {

        @Override
        protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
            System.out.println("写出");
            StringBuilder sb = new StringBuilder();
            out.add(bytes);
            out.add(bytes);
            out.add(getBytes(0));
            out.add(getBytes(1));
            out.add(getBytes(1));
            out.add(getBytes(4));
            for (int i = 1; i < 18; i++) {
                int finalI = 0;
                out.add(getBytes(finalI));
            }
            out.add(getBytes(6, 2));
            String[] split = msg.split(":");
            String s = split[0].replaceAll("\\.", "");
            String s1 = split[1];
            out.add(getBytes(Integer.valueOf(s1), 2));
            out.add(getBytes(Integer.valueOf(s), 4));
            sb.append(formatStringWithZero(Integer.toBinaryString(35), 1))
                .append(formatStringWithZero(Integer.toBinaryString(35), 1))
                .append(formatStringWithZero(Integer.toBinaryString(0), 1))
                .append(formatStringWithZero(Integer.toBinaryString(1), 1))
                .append(formatStringWithZero(Integer.toBinaryString(1), 1))
                .append(formatStringWithZero(Integer.toBinaryString(4), 1));
            for (int i = 1; i < 18; i++) {
                int finalI = 0;
                sb.append(formatStringWithZero(Long.toBinaryString(finalI), 1));
            }
            sb.append(formatStringWithZero(Integer.toBinaryString(6), 2))
                .append(formatStringWithZero(Integer.toBinaryString(Integer.valueOf(s1)), 2))
                .append(formatStringWithZero(Integer.toBinaryString(Integer.valueOf(s)), 4));
            Integer bbc = getBBC(sb.toString());
            out.add(getBytes(bbc));
            System.out.println("写出完成");
        }
    }

    public static List<Integer> bytesToHexString(String s) {
        int length = s.length();
        if (s == null || length <= 0) {
            return null;
        }
        List<Integer> list = getList(s);
        Integer result = getInteger(list);
        if (result.equals(list.get(list.size() - 1))) {
            return list;
        }
        return null;
    }

    private static List<Integer> getList(String s) {
        List<Integer> list = Lists.newArrayList();
        int readIndex = 0;
        while (readIndex < s.length()) {
            list.add(Integer.valueOf(s.substring(readIndex, readIndex + 8), 2));
            readIndex += 8;
        }
        return list;
    }

    private static Integer getInteger(List<Integer> list) {
        Integer result = list.get(0);
        for (int i = 1; i < list.size() - 1; i++) {
            result = result ^ list.get(i);
        }
        return result;
    }

    private static Integer getBBC(String s) {
        List<Integer> list = getList(s);
        Integer integer = getInteger(list);
        return integer;
    }

    public static String formatStringWithZero(String s, int byteSize) throws Exception {
        if (byteSize * 8 - s.length() < 0) {
            throw new Exception("超位");
        }
        return Stream.generate(() -> "0").limit(byteSize * 8 - s.length()).collect(Collectors.joining()) + s;
    }

    private byte[] getBytes(Supplier<String> supplier, int byteSize) throws Exception {
        return formatStringWithZero(supplier.get(), byteSize).getBytes();
    }

    private byte[] getBytes(int i, int byteSize) throws Exception {
        return getBytes(() -> Integer.toBinaryString(i), byteSize);
    }

    private byte[] getBytes(int i) throws Exception {
        return getBytes(() -> Integer.toBinaryString(i), 1);
    }
}
