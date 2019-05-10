package com.lft.netty.test_2018_12_12.webSocket;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 处理 http
 * ①以/ws 结尾的下个 handler 处理
 * ②其他的http 就在这响应了
 * @author Ryze
 * @date 2018-12-12 15:35
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String url;
    private static File index;

    static {
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            index = new File(path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public HttpRequestHandler(String url) {
        this.url = url;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        //如果请求了  以/ws http 请求  websocket升级
        if (url.equalsIgnoreCase(request.uri())) {
            //把 request 转给 下个 channelRead0 方法处理
            ctx.fireChannelRead(request.retain());
        } else {
            //没有请求 那么按http 1.0 处理
            if (HttpUtil.is100ContinueExpected(request)) {
                send100Http(ctx);
            }
            RandomAccessFile file = new RandomAccessFile(index, "r");
            //构建 response
            DefaultHttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, index.length());
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            //写出响应
            ctx.write(response);
            //写出资源 如果支持的话先使用zero copy
            if (ctx.pipeline().get(SslHandler.class) == null) {
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            //写并冲刷数据
            ChannelFuture channelFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            //如果没有请求 keep alive 那么就关了它
            if (!keepAlive) {
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void send100Http(ChannelHandlerContext ctx) {
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(defaultFullHttpResponse);
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
