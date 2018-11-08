package org.lingg.jdk.hashmap.key;

import java.util.IdentityHashMap;
import java.util.Map;

public class TestIdentityHashMap {
	public static void main(String[] args) {

		String str1 = new String("xx");
		String str2 = new String("xx");
		System.out.println(str1 == str2);
		System.err.println(str1.hashCode() == str2.hashCode());
		System.err.println(str1.equals(str2));

		//此类不是 通用 Map 实现！此类实现Map 接口时，它有意违反 Map 的常规协定，该协定在比较对象时强制使用equals 方法。此类设计仅用于其中需要引用相等性语义的罕见情况。
		Map<String ,String> map = new IdentityHashMap<String ,String>();
		map.put(str1, "hello");
		map.put(str2, "world");


		for(Map.Entry<String,String> entry : map.entrySet())
		{
			System.out.println(entry.getKey()+"   " + entry.getValue());
		}
		System.out.println("     containsKey---> " + map.containsKey("xx"));
		System.out.println("str1 containsKey---> " + map.containsKey(str1));
		System.out.println("str2 containsKey---> " + map.containsKey(str2));
		System.out.println("  	  value----> " + map.get("xx"));
		System.out.println("str1  value----> " + map.get(str1));
		System.out.println("str2  value----> " + map.get(str2));

		System.out.println("xx" == str1);
		System.out.println("xx" == str1);
		System.out.println("xx" == "xx");

	}
}

//---------------------
//作者：技术特工队
//来源：CSDN
//原文：https://blog.csdn.net/wx_962464/article/details/7701141
//版权声明：本文为博主原创文章，转载请附上博文链接！