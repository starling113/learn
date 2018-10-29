package org.lingg.learn.pattern.decorator;

/**
 * 具体构件角色（Concrete Component）： 定义一个将要接收附加责任的类。
 */
public class ConcreteComponent implements Component
{
	@Override
	public void doSomething()
	{
		System.out.println("功能A");
	}

}
