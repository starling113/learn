package org.lingg.learn.pattern.proxy.dynamicproxy;

public class RealSuject implements Subject {

	public void request() {
		System.out.println("dynamic from request");
	}

}
