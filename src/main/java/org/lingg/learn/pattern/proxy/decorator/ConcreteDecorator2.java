package org.lingg.learn.pattern.proxy.decorator;

/**
 * 具体装饰角色（Concrete Decorator）：负责给构件对象“贴上”附加的责任。
 */
public class ConcreteDecorator2 extends Decorator
{
	public ConcreteDecorator2(Component component)
	{
		super(component);
	}
	
	@Override
	public void doSomething()
	{
		super.doSomething();
		
		this.doAnotherThing();
	}
	
	private void doAnotherThing()
	{
		System.out.println("功能C");
	}
}
