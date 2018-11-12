package org.lingg.learn.redisInAction.redislock;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lingg.learn.redisInAction.book.RedisConst;
import redis.clients.jedis.Jedis;

public class LockTest {

    static Jedis conn;

    @BeforeAll
    public static void init() {
        conn = new Jedis(RedisConst.redisHost);
//        conn.select(RedisConst.redisDbIndex);
    }

    @Test
    public void testLock() {
        System.out.println("user01获取锁：" + RedisLockTool.tryGetDistributedLock(conn, "resource01", "user01", 10 * 1000));
    }

    @Test
    public void testLock2() {
        System.out.println("user02获取锁：" + RedisLockTool.tryGetDistributedLock(conn, "resource01", "user02", 10 * 1000));
    }

    @Test
    public void testRelease() {
        System.out.println("user01释放锁：" + RedisLockTool.releaseDistributedLock(conn, "resource01", "user01"));
    }
    @Test
    public void testRelease2() {
        System.out.println("user02释放锁：" + RedisLockTool.releaseDistributedLock(conn, "resource01", "user02"));
    }
}
