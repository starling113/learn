package org.lingg.learn.netty4.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
/**
 * 服务端消息处理
 * @author -琴兽-
 *
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

	protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("channelRead0 : " + msg);

//       ctx.channel().writeAndFlush("hi");
        ctx.writeAndFlush("服务器收到"+ctx.channel().remoteAddress()+"发送的消息："+msg); //返回消息
	}

	/**
	 * 新客户端接入
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelActive");
	}

	/**
	 * 客户端断开
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelInactive");
	}

	/**
	 * 异常
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
	}

    /**
     * netty4 接收到消息 netty5中方法修改成了messageReceived
     * @param channelHandlerContext
     * @param s
     * @throws Exception
     */
	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
		messageReceived(channelHandlerContext,s);
	}
}
