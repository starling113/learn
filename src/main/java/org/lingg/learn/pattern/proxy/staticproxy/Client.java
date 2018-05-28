package org.lingg.learn.pattern.proxy.staticproxy;

public class Client {
	public static void main(String[] args) {
		Subject s = new ProxySubject();
		s.request();
	}
}
