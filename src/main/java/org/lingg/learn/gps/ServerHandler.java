package org.lingg.learn.gps;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 服务端消息处理
 *
 */
public class ServerHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        System.out.println("buffer.readableBytes() = " + buffer.readableBytes());
        if(buffer.readableBytes() > 4){

            if(buffer.readableBytes() > 2048){
                buffer.skipBytes(buffer.readableBytes());
            }

            //标记
            buffer.markReaderIndex();
            //长度
            int length = buffer.readInt();

            if(buffer.readableBytes() < length){
                buffer.resetReaderIndex();
                //缓存当前剩余的buffer数据，等待剩下数据包到来
                return;
            }

            //读数据
            byte[] bytes = new byte[length];
            buffer.readBytes(bytes);
            //往下传递对象
            ctx.fireChannelRead(new String(bytes));
        }
        //缓存当前剩余的buffer数据，等待剩下数据包到来
        return;
    }
}
