package org.lingg.learn.netty4all.netty4.serverAndClientCommumication;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 客户端消息处理
 * @author -琴兽-
 *
 */
public class TcpClientHandler extends MessageToByteEncoder<String> {


	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
		System.out.println("客户端收到消息:"+msg);
	}
}
