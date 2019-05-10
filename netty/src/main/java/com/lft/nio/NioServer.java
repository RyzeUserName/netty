package com.lft.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) throws IOException {
        start();
    }

    private static void start() {
        ServerSocketChannel socketChannel = null;
        SelectionKey selectionKey = null;
        try {
            socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);
            ServerSocket socket = socketChannel.socket();
            //绑定端口
            socket.bind(new InetSocketAddress(9000));
            // 打开 Selector 处理channel
            Selector selector = Selector.open();
            // 注册到 Selector 上接收连接
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            final ByteBuffer wrap = ByteBuffer.wrap("hello !".getBytes());
            for (; ; ) {
                // 等待需要处理事件新事件（阻塞到下一事件传入）
                selector.select();
                //获取所有事件的 selectionKey 实例
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    selectionKey = iterator.next();
                    iterator.remove();
                    //检查 事件是否是一个新的已经就绪可以被接受的连接
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel accept = channel.accept();
                        accept.configureBlocking(false);
                        //接收客户端 并将它注册到选择器
                        accept.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, wrap.duplicate());
                        System.out.println("收到连接  来自 " + accept);
                        // 检查套接字是否准备好 写数据
                        if (selectionKey.isWritable()) {
                            SocketChannel client = (SocketChannel) selectionKey.channel();
                            ByteBuffer attachment = (ByteBuffer) selectionKey.attachment();
                            while (attachment.hasRemaining()) {
                                //写数据
                                if (client.write(attachment) == 0) {
                                    break;
                                }
                            }
                            //关闭连接
                            client.close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            try {
                selectionKey.cancel();
                selectionKey.channel().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }


    }

}
