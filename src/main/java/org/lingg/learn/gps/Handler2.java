package org.lingg.learn.gps;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 处理服务端 channel.
 */
public class Handler2 extends ChannelInboundHandlerAdapter { // (1)

    int cnt = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)

        System.err.println(msg+"  "+(++cnt));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
