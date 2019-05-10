package com.lft.netty.test_2018_12_12.webSocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * webSocket 文本数据的帧处理
 * @author Ryze
 * @date 2018-12-12 16:28
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //处理自定义 事件 握手完成
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            //移除http 处理
            ctx.pipeline().remove(HttpRequestHandler.class);
            group.writeAndFlush(new TextWebSocketFrame("client " + ctx.channel() + "加入"));
            //加入的话 放到ChannelGroup 一起管理
            group.add(ctx.channel());
        } else {
            //否则 就默认处理
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //写进 整个ChannelGroup 里面的channel都会写出这些信息  并增加引用计数
        group.writeAndFlush(msg.retain());
    }
}
