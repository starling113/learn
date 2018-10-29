package org.lingg.learn.pattern.decorator;

/**
 * 装饰角色（Decorator）：持有一个构件（Component）对象的引用，并定义一个与抽象构件接口一致的接口
 */
public class Decorator implements Component
{
	private Component component;
	
	public Decorator(Component component)
	{
		this.component = component;
	}
	
	@Override
	public void doSomething()
	{
		component.doSomething();
	}
}
