package com.lft.netty.test_2018_12_6;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
//
///**
// * 谷歌的序列化
// * @author Ryze
// * @date 2018-12-07 0:27
// */
//public class ProtoBufInitializer extends ChannelInitializer<Channel> {
//    @Override
//    protected void initChannel(Channel ch) throws Exception {
//        ch.pipeline()
//            .addLast(new ProtobufVarint32FrameDecoder())
//            .addLast(new ProtobufEncoder())
//            .addLast(new ProtobufDecoder(new MessageLite()));
//    }
//}
