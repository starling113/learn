package org.lingg.learn.pattern.proxy.staticproxy;

public class RealSubject implements Subject {

	@Override
	public void request() {
		System.out.println("real subject");
	}

}
