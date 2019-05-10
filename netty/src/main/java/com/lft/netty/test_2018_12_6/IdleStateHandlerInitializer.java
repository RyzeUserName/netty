package com.lft.netty.test_2018_12_6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 处理闲置
 * @author Ryze
 * @date 2018-12-06 17:21
 */
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            //超时之后会触发一个 IdleStateEvent 事件
            .addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS))
            .addLast(new HeartbeatHandler());

    }

    private static class HeartbeatHandler extends ChannelInboundHandlerAdapter {
        private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(
            "HEARTBEAT", CharsetUtil.ISO_8859_1
        ));

        /**
         * 对事件进行处理
         */
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                //发送一个心跳  失败的话 关闭连接
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                    .addListener(ChannelFutureListener.CLOSE);
            }else {
                super.userEventTriggered(ctx,evt);
            }
        }
    }
}
