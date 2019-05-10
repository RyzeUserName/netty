package com.lft.netty.test_2018_12_6;

import io.netty.channel.Channel;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * zero copy
 * @author Ryze
 * @date 2018-12-06 18:40
 */
public class ZeroCopy {
    public static void main(String[] args) {
        try {
            File file = new File("文件地址");
            FileInputStream in = new FileInputStream(file);
            DefaultFileRegion defaultFileRegion = new DefaultFileRegion(in.getChannel(), 0, file.length());
            Channel channel = new NioServerSocketChannel();
            channel.writeAndFlush(defaultFileRegion)
                .addListener(future -> {
                    if (!future.isSuccess()) {
                        //异常处理
                        future.cause().printStackTrace();
                    }
                });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
