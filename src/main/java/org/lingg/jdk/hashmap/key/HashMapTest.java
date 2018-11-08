package org.lingg.jdk.hashmap.key;

import org.apache.kafka.common.protocol.types.Field;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
 
/**
* @author Bingo 
* E-mail:riskys@163.com
* @version 创建时间：2017年7月3日 上午9:57:49
* 类说明
*/
public class HashMapTest {
 
	public static void main(String[] args) {
		Map<String, Dog> map = new HashMap<>();
		map.put("zhang", new Dog("zhangmao", 4));
		map.put("wang", new Dog("wanggou", 3));
		map.put("zhang", new Dog("zhanggou", 2));
		map.put("zhang", new Dog("zhangzhu", 1));
		map.put("wang", new Dog());
		
		for (String key : map.keySet()) {
			System.out.println(key +" :  " + map.get(key));
		}
	}
	
	private static class Dog {
		private String name;
		private int age;
		
		public Dog() {
			this("default name", 0);
		}
		public Dog(String name, int age) {
			this.name = name;
			this.age = age;
		}
		@Override
		public String toString() {
			return ""+name + " " + age+"";
		}
	}

	@Test
    public void testadd(){
	    String aa = "aa";
	    String bb = "aa";

        System.out.println(aa.hashCode());
        System.out.println(bb.hashCode());

        System.out.println(aa == bb);

        Map<String, String> map = new HashMap<>(4);
        map.put(aa, "abc");
        map.put(bb, "def");

        System.out.println(map.size());
        System.out.println(map.get("aa"));
        System.out.println(map.get(aa));
        System.out.println(map.get(bb));


        //hashmap put
//        拿到了hash值后，调用 putVal()，做了如下操作
//        将对象table赋值给tab，并以tab是否为空作为是否第一次调用此方法的判断，是则resize()并给tab，n赋值；
//        获取tab的第i个元素：根据 (n - 1) & hash 算法 ，计算出i找到，如果为空，调用newNode() ，赋值给tab第i个；
//        如果不为空，可能存在2种情况：hash值重复了，也就是put过程中，发现之前已经有了此key对应的value，则暂时e = p；
//        至于另外一种情况就是位置冲突了，即根据(n - 1) & hash算法发生了碰撞，再次分情况讨论；
//        1.以链表的形式存入；
//        2.如果碰撞导致链表过长(大于等于TREEIFY_THRESHOLD)，就把链表转换成红黑树；
//        最后，如果e不为空，将e添加到table中（e.value 被赋值为 putVal()中的参数 value）；
    }
}
