package org.lingg.learn.pattern.adapter;

/**
 * 适配器模式包含一下三个角色：
 *
 * 　　1：Target(目标抽象类)：目标抽象类定义客户所需的接口，可以是一个抽象类或接口，也可以是具体类。在类适配器中，由于C#语言不支持多重继承，所以它只能是接口。
 *
 * 　　2：Adapter(适配器类)：它可以调用另一个接口，作为一个转换器，对Adaptee和Target进行适配。它是适配器模式的核心。
 *
 * 　　3：Adaptee(适配者类)：适配者即被适配的角色，它定义了一个已经存在的接口，这个接口需要适配，适配者类包好了客户希望的业务方法。
 */
public class Adapter extends Adaptee implements Target
{
	@Override
	public void method1()
	{
		this.method2();
	}

}
