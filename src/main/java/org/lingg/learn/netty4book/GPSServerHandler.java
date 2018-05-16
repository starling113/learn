package org.lingg.learn.netty4book;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 处理服务端 channel.
 */
public class GPSServerHandler extends ChannelInboundHandlerAdapter { // (1)

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        System.err.println(msg.getClass());

        ByteBuf byteBuf = (ByteBuf) msg;
        System.err.println(byteBuf.readableBytes());

        // 默默地丢弃收到的数据
        ((ByteBuf) msg).release(); // (3)

        ctx.fireChannelRead("second");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
