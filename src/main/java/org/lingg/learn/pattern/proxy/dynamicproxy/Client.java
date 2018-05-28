package org.lingg.learn.pattern.proxy.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Client {
	public static void main(String[] args) {
		RealSuject rs = new RealSuject();
		
		InvocationHandler ih = new DynamicSubject(rs);
		
		Class<?> clazz = ih.getClass();
		
		Subject s = (Subject) Proxy.newProxyInstance(
				clazz.getClassLoader(),
				rs.getClass().getInterfaces(), ih);
		
		s.request();
		
		System.out.println(s.getClass());
	}
}
