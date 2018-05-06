package org.lingg.learn.netty4all.netty3.heart;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleStateEvent;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 消息接受处理类
 *
 * @author -琴兽-
 */
public class IdleHandler extends SimpleChannelHandler {

    /**
     * 接收消息
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String s = (String) e.getMessage();
        System.out.println(s);

        //回写数据
        ctx.getChannel().write("hi");
        super.messageReceived(ctx, e);
    }

    /**
     * 捕获异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        System.out.println("exceptionCaught");
        super.exceptionCaught(ctx, e);
    }

    /**
     * 新连接
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelConnected");
        super.channelConnected(ctx, e);
    }

    /**
     * 必须是链接已经建立，关闭通道的时候才会触发
     */
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelDisconnected");
        super.channelDisconnected(ctx, e);
    }

    /**
     * channel关闭的时候触发
     */
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelClosed");
        super.channelClosed(ctx, e);
    }

    /**
     * {@inheritDoc}  Down-casts the received upstream event into more
     * meaningful sub-type event and calls an appropriate handler method with
     * the down-casted event.
     *
     * @param ctx
     * @param e
     */
    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        //System.out.println(e);
        String nowStr = DateTimeFormatter.ofPattern("hh:mm:ss").format(LocalTime.now());
        if (e instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) e;
            switch (event.getState()) {
                case WRITER_IDLE:
                case READER_IDLE:
                    System.out.println(event.getState() + "   " + nowStr);
                    break;
                case ALL_IDLE:
                    //关闭会话,踢玩家下线
                    ChannelFuture channelFuture = ctx.getChannel().write("timeout, you will close!!!");
//                    channelFuture.addListener(new ChannelFutureListener() {
//                        @Override
//                        public void operationComplete(ChannelFuture future) throws Exception {
//                            ctx.getChannel().close();
//                        }
//                    });
                    channelFuture.addListener(future -> ctx.getChannel().close());
                    break;
                default:
                    System.out.println("other idle");
            }
        } else {
            super.handleUpstream(ctx, e);
        }
    }
}
