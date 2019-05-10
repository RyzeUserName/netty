package com.lft.netty.test_2018_12_6;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * http 压缩  一般都是客户端压缩
 *            服务端压缩则客户端需要支持
 * @author Ryze
 * @date 2018-12-06 16:29
 */
public class HttpCompressionInitializer extends ChannelInitializer<Channel> {
    private final boolean isClient;

    public HttpCompressionInitializer(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //如果是客户端
        if (isClient) {
            pipeline.addLast("codec", new HttpClientCodec())
                //解压
                .addLast("decompression", new HttpContentDecompressor());
        } else {
            pipeline.addLast("codec", new HttpServerCodec())
                //压缩
                .addLast("compression", new HttpContentCompressor());
        }
    }
}
