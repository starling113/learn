package org.lingg.jdk.hashmap.key;

import java.util.HashMap;
import java.util.Map;

public class TestHashMap {
    public static void method1() {

        Person p1 = new Person("xiaoer", 1);
        Person p2 = new Person("san", 4);

//        System.err.println(p1.hashCode());
//        System.err.println(p2.hashCode());
//        System.err.println(p1.equals(p2));

        Map<Person, String> maps = new HashMap<Person, String>();
        maps.put(p1, "aaa");
        maps.put(p2, "bbb");
        System.out.println(maps);
//
        maps.put(p2, "ccc");
        System.out.println(maps);
        System.out.println(p1.hashCode());
        p1.setAge(5);
        System.out.println(p1.hashCode());
        System.out.println(maps);
//
        maps.put(p1, "ddd");
//
        System.out.println(p1.hashCode());  //hashcode 已经变化，但是之前放的p1在map中位置没有变
//
//        System.out.println(p1.hashCode());
        System.out.println(maps);
        System.out.println(maps.get(p1));
    }

    public static void main(String[] args) {
        method1();
    }
}