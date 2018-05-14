package org.lingg.learn.netty4all.netty4.udp.writeToEach;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 * @author 作者 YYD
 * @version 创建时间：2016年11月18日 下午9:09:18
 * @function 未添加
 */
public class ChineseProverClientHandler extends
        SimpleChannelInboundHandler<DatagramPacket> {
/**
 * DatagramPacket的详细介绍，看服务器的代码注释，这里不重复了。
 */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
            throws Exception {
        String response = msg.content().toString(CharsetUtil.UTF_8);
        if (response.startsWith("谚语查询结果：")) {
            System.out.println(response);
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}