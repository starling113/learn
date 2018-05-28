package org.lingg.learn.pattern.proxy.staticproxy;

public class ProxySubject implements Subject {
	private RealSubject subject;

	
	
	@Override
	public void request() {
		
		if(null == subject){
			subject = new RealSubject();
		}
		
		System.out.println("before");
		subject.request();
		System.out.println("after");
	}
}
