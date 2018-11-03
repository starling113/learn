package org.lingg.jdk;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HashMapTest {
    public static void main(String[] args) {
        HashMap<Long, Long> map = new HashMap<>();

//        System.out.println(map.put("aa", "AA1"));
//        System.out.println(map.put("aa", "AA2"));
//        System.out.println(map.put("aa", "AA3"));
//
//        map.put("bb", "BB");
//        map.put("cc", "CC");
//
//        System.out.println(map.get("bb"));



        ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.submit(new Runnable() {
            @Override
            public void run() {
                Long key = 0L;
                while (key < 9999999L) {
                    map.put(key, key);
                    key++;
                }
            }
        });

        pool.submit(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    Long val = map.get(Long.valueOf(55));
                    if (null==val || val != 55) {
                        System.out.println("扩容，多线程冲突，key : 55 val :" + val);
                    }
                }
            }
        });


    }
}
