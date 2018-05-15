package org.lingg.learn.redisInAction;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Iterator;
import java.util.Set;

public class RedisTest1 {

    private static Jedis conn = null;

    @BeforeAll
    public static void beforeAll(){
        conn = new Jedis("192.168.163.130");
        conn.select(8);
    }

    @Test
    public void test1(){
        conn.zincrby("worker", -1, "tom");

        Set<Tuple> workers = conn.zrangeWithScores("worker", 0, -1);

        Iterator<Tuple> iterator = workers.iterator();
        while(iterator.hasNext()){
            Tuple w = iterator.next();
            System.out.println(w.getElement()+" \t "+w.getScore());
        }
    }

    @AfterAll
    public static  void afterall(){
        conn.close();
    }
}
