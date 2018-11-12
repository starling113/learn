package org.lingg.jdk.hashmap.key;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class KeyEqTest {

    class MyTT{
        int age ;

        @Override
        public boolean equals(Object obj) {
            return true;
        }

        @Override
        public int hashCode() {
            return age;
        }

        public MyTT(int age){
            this.age =age;
        }
    }

    @Test
    public void test(){
        MyTT t1 = new MyTT(4);
        MyTT t2 = new MyTT(4);

        System.out.println(t1.equals(t2));
        System.out.println("t1 hashcode : "+t1.hashCode());
        System.out.println("t2 hashcode : "+t2.hashCode());

        Map<MyTT, Object> map = new HashMap<>();

        map.put(t1, "hello");
        map.put(t2, "world");

        System.out.println(map.size());
        System.out.println(map.get(t1));
        System.out.println(map.get(t2));
    }
}
