package org.lingg.learn.netty4.heart;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 服务端消息处理
 * @author -琴兽-
 *
 */
public class HeartHandler extends SimpleChannelInboundHandler<String> {

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

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleStateEvent event = (IdleStateEvent)evt;
			if(event.state() == IdleState.ALL_IDLE){
				ChannelFuture channelFuture = ctx.channel().writeAndFlush("you will close");//需要flush
				channelFuture.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
                        ctx.channel().close();
					}
				});
			}
		}else{
			super.userEventTriggered(ctx, evt);
		}
	}
}
