package org.lingg.learn.netty4all.protobuf.serial;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteBufferSerialTest {

	public static void main(String[] args) {
		int id = 101;
		int age = 21;
		
		ByteBuffer buffer = ByteBuffer.allocate(8); //容纳数据固定
		buffer.putInt(id);
		buffer.putInt(age);
		byte[] array = buffer.array();
		System.out.println(Arrays.toString(buffer.array()));
		
		//====================================================
		
		ByteBuffer buffer2 = ByteBuffer.wrap(array);
		System.out.println("id:"+buffer2.getInt());
		System.out.println("age:"+buffer2.getInt());

	}

}
