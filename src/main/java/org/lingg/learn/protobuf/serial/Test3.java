package org.lingg.learn.protobuf.serial;


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.util.Arrays;

public class Test3 {

	public static void main(String[] args) {

		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeInt(101);
		buffer.writeDouble(80.1);

		byte[] bytes = new byte[buffer.writerIndex()];
		buffer.readBytes(bytes);
		
		System.out.println(Arrays.toString(bytes));

		//================================================
		ChannelBuffer wrappedBuffer = ChannelBuffers.wrappedBuffer(bytes);
		System.out.println(wrappedBuffer.readInt());
		System.out.println(wrappedBuffer.readDouble());
		
	}

}
