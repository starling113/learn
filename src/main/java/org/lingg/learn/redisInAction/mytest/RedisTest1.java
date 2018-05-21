package org.lingg.learn.redisInAction.mytest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisTest1 {

//    private static Jedis conn = null;

//    @BeforeAll
//    public static void beforeAll(){
//        conn = new Jedis("192.168.163.130");
//        conn.select(8);
//    }

//    @AfterAll
//    public static  void afterall(){
//        conn.close();
//    }
    @Test
    public void test1(){
        Jedis conn = new Jedis("192.168.163.130");
        conn.zincrby("worker", -1, "tom");

        Set<Tuple> workers = conn.zrangeWithScores("worker", 0, -1);

        Iterator<Tuple> iterator = workers.iterator();
        while(iterator.hasNext()){
            Tuple w = iterator.next();
            System.out.println(w.getElement()+" \t "+w.getScore());
        }
    }

    @Test
    public void testTran(){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Jedis conn = new Jedis("192.168.163.130");
                System.out.println(Thread.currentThread().getName()+" "+ conn.get("tran"));
                Transaction tran = conn.multi();
                tran.incr("tran");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tran.incrBy("tran", -1);
                List<Object> exec = tran.exec();
                System.out.println(exec);
            }
        };

        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Jedis conn = new Jedis("192.168.163.130");
        System.out.println(conn.get("tran"));
    }




}
