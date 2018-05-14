package org.lingg.learn.netty4all.netty4.udp.writeToEach;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * @author 作者 YYD
 * @version 创建时间：2016年11月18日 下午8:38:30
 * @function 未添加
 */
public class UdpServer {
    public void run(int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        //由于我们用的是UDP协议，所以要用NioDatagramChannel来创建
        b.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)//支持广播
                .handler(new ChineseProverbServerHandler());//ChineseProverbServerHandler是业务处理类
        b.bind(port).sync().channel().closeFuture().await();
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new UdpServer().run(port);
    }
}