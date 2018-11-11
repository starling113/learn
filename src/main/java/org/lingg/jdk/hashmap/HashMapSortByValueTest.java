package org.lingg.jdk.hashmap;

import java.util.*;

class HashMapSortByValueTest
{
	private static class ValueComparator implements Comparator<Map.Entry<String,Integer>>
	{
		public int compare(Map.Entry<String,Integer> m, Map.Entry<String,Integer> n)
		{
			return n.getValue()-m.getValue();  //按值来排序 倒序
//			return m.getKey().compareTo(n.getKey()); //按key来排序

		}
	}
	public static void main(String[] args) 
	{
		Map<String,Integer> map=new HashMap<>();
		map.put("a",1);
		map.put("c",3);
		map.put("b",5);
		map.put("f",7);
		map.put("e",6);
		map.put("d",8);
		List<Map.Entry<String,Integer>> list=new ArrayList<>();

		list.addAll(map.entrySet());

		Collections.sort(list,new ValueComparator());

		for(Iterator<Map.Entry<String,Integer>> it=list.iterator();it.hasNext();)
		{
			System.out.println(it.next());
		}
	}
}
//---------------------
//作者：吴孟达
//来源：CSDN
//原文：https://blog.csdn.net/exceptional_derek/article/details/9852929
//版权声明：本文为博主原创文章，转载请附上博文链接！