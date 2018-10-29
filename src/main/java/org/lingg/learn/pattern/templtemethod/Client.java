package org.lingg.learn.pattern.templtemethod;

public class Client
{
	public static void main(String[] args)
	{
		AbstractClass ac = new ConcreteClass();
		
		ac.template();
	}
}
