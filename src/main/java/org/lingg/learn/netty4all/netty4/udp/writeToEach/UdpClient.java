package org.lingg.learn.netty4all.netty4.udp.writeToEach;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author 作者 YYD
 * @version 创建时间：2016年11月18日 下午9:00:11
 * @function 未添加
 */
public class UdpClient {
    public void run(int port) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)//允许广播
                    .handler(new ChineseProverClientHandler());//设置消息处理器

            Channel ch = b.bind(0).sync().channel();
            //向网段内的所有机器广播UDP消息。
            ch.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("谚语字典查询？", CharsetUtil.UTF_8), new InetSocketAddress("255.255.255.255", port))).sync();

            //255.255.255.255
            //这个IP是个广播地址。就是我要发东西出去，我发到这地址上，所有其它电脑都会接收这个数据

            if (!ch.closeFuture().await(15000)) {
                System.out.println("查询超时！");
            }
        } catch (Exception e) {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;

        new UdpClient().run(port);
    }
}