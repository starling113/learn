package org.lingg.learn.netty4all.pipeline;

import java.net.Socket;

public class Client {

	public static void main(String[] args) throws Exception {

		Socket socket = new Socket("127.0.0.1", 10101);
		
		socket.getOutputStream().write("hello".getBytes());
		
		socket.close();
	}

}
