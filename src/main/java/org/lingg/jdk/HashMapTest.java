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

        ExecutorService pool = Executors.newFixedThreadPool(3);

        pool.submit(() -> {

                    Long key = 0L;
                    while (key < 9999999L) {
                        map.put(key, key);
                        key++;
                    }
                }
        );

        pool.submit(() -> {
                    while (true) {
                        Long val = map.get(Long.valueOf(5));
                        if (null == val || val != 5) {
                            System.out.println("扩容，多线程冲突，key : 5 val :" + val);
                        }
                    }
                }
        );

        pool.submit(() -> {
            try {
                Thread.sleep(2000L);
                System.err.println(map.get(5));
            } catch (Exception ex) {

            }
        });
    }
}
