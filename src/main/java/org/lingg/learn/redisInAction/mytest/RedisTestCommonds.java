package org.lingg.learn.redisInAction.mytest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisTestCommonds {

    private static String redisHost = "192.168.163.130";

    @BeforeAll
    public static void beforeAll(){

    }

    @Test
    public void testIncrBY(){
        Jedis conn = new Jedis(redisHost);
        conn.select(7);
        conn.incrBy("cnt", 5);
        String cnt = conn.get("cnt");
        conn.close();
        System.out.println(cnt);
    }

    @Test
    public void testTran(){

        Jedis conn = new Jedis(redisHost);
        conn.set("cnt", "1");


        new Thread(() -> {
            Jedis conn2 = new Jedis(redisHost);
            System.out.println(Thread.currentThread().getName()+" "+conn2.get("cnt"));
            conn2.watch("cnt");

            Transaction tran = conn2.multi();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tran.incr("cnt");
            System.out.println(tran.get("cnt"));
            List<Object> exec = tran.exec();
            System.out.println(exec);

            conn2.close();
        }).start();

        new Thread(()->{
            Jedis conn2 = new Jedis(redisHost);
            System.out.println(Thread.currentThread().getName()+" "+conn2.get("cnt"));
            conn2.watch("cnt");

            Transaction tran = conn2.multi();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tran.incr("cnt");
            System.out.println(tran.get("cnt"));
            List<Object> exec = tran.exec();
            System.out.println(exec);

            conn2.close();
        }).start();

        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName()+" "+conn.get("cnt"));
        conn.close();
    }

    @AfterAll
    public static  void afterall(){

    }
}
