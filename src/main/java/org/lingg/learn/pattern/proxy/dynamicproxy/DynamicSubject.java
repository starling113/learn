package org.lingg.learn.pattern.proxy.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicSubject implements InvocationHandler {

	private Object realSubject;
	
	public DynamicSubject(Object obj){
		this.realSubject = obj;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		System.out.println("before " + method);
		
		method.invoke(realSubject, args);
		
		System.out.println("after " + method);
		
		return null;
	}

}
