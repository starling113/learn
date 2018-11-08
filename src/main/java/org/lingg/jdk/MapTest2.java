package org.lingg.jdk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapTest2 {

    public static void main(String[] args) {
        String a1 = new String("aa") ;
        String a2 = new String("aa") ;

        Map<String, Object> map = new HashMap<>();
        map.put(a1,a1);
        map.put(a2, a2);

        System.out.println(map.size());

        System.out.println(a1 == a2);
        System.out.println(a1.hashCode());
        System.out.println(a2.hashCode());

        Set<String> set = new HashSet<>();
        set.add(a1);
        set.add(a2);
        System.out.println(set.size());

    }
}
