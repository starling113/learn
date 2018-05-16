package org.lingg.learn.netty4book;

import java.net.Socket;

public class Client {

	public static void main(String[] args) throws Exception {

		Socket socket = new Socket("127.0.0.1", 10101);
		
		socket.getOutputStream().write("xdxd".getBytes());
		
		socket.close();
	}

}
