package com.lft.netty.test_2018_12_12.webSocket;

import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

/**
 * 加密服务器 https://localhost:9999
 * @author Ryze
 * @date 2018-12-12 19:03
 */
@SuppressWarnings("all")
public class SecurityChatServer extends ChatServer {
    private final SslContext sslContext;

    public SecurityChatServer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    public ChatServerInitializer createInitializer(ChannelGroup channelGroup) {
        return new SecurityChatServerInitializer(channelGroup, sslContext);

    }

    public static void main(String[] args) throws CertificateException, SSLException {
        SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
        SslContext build = SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey()).build();
        SecurityChatServer chatServer = new SecurityChatServer(build);
        ChannelFuture start = chatServer.start(new InetSocketAddress(Integer.valueOf(9999)));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> chatServer.destroy()));
        start.channel().closeFuture().syncUninterruptibly();
    }
}
