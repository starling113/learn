package org.lingg.learn.netty4all.netty4.serverAndClientCommumication;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 服务端消息处理
 */
public class TcpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
        System.err.println("server receive : "+ msg);

//        ByteBuf buf = ctx.alloc().buffer();
//        buf.writeBytes(("server端返回xxx"+msg).getBytes());
//        ctx.writeAndFlush(buf);

        ctx.writeAndFlush("hello 我是服务端");
    }
}
