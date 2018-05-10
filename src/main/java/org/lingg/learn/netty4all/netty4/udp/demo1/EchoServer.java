package org.lingg.learn.netty4all.netty4.udp.demo1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class EchoServer {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioDatagramChannel.class)
                .handler(new EchoSeverHandler());

        // 服务端监听在9999端口
        b.bind(ConfigEntity.serverUDPPort).sync().channel().closeFuture().await();
    }
}