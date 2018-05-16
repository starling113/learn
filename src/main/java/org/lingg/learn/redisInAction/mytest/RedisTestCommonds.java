package org.lingg.learn.redisInAction.mytest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

public class RedisTestCommonds {

    private static Jedis conn = null;

    @BeforeAll
    public static void beforeAll(){
        conn = new Jedis("192.168.163.130");
        conn.select(7);
    }

    @Test
    public void testIncrBY(){
        conn.incrBy("cnt", 5);
        String cnt = conn.get("cnt");
        System.out.println(cnt);
    }

    @AfterAll
    public static  void afterall(){
        conn.close();
    }
}
