package org.lingg.learn.netty4all.packet;

import java.net.Socket;
import java.nio.ByteBuffer;

public class Client {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 10101);

        String message = "hello测试内容";

        byte[] bytes = message.getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(5 + 4 + bytes.length);
        buffer.put("anxin".getBytes());
        buffer.putInt(bytes.length);
        buffer.put(bytes);

        byte[] array = buffer.array();

        for (int i = 0; i < 1000; i++) {
            socket.getOutputStream().write(array);
        }

        socket.close();
    }
}
