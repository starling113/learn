package org.lingg.learn.redisInAction.redislock;

import redis.clients.jedis.Jedis;

// https://hacpai.com/article/1513007414553

public class RedisReentryLockTool {

    /**
     * 获得锁 lua 脚本
     * 三个参数：key、线程标识、超时时间
     */
    public static String LOCK_SCRIPT = "local f = redis.call('HGET',KEYS[1],'flag')\n" + //获得持有锁的人，加锁的资源是keys[1]
            "-- 如果线程标识不是空 且不是当前线程 则返回加锁失败\n" +
            "if type(f) == 'string' and f ~= KEYS[2] then\n" + // keys[2] 申请锁的人， f是redis里面现在持有锁的人
            "    return 0\n" + // 不是同一人，返回0
            "end\n" +
            "-- 设置线程标识\n" +
            "redis.call('HSET',KEYS[1],'flag',KEYS[2])\n" + // 设置资源keys[1]，持有锁的人是KEYS[2]
            "-- 设置超时时间\n" +
            "redis.call('EXPIRE',KEYS[1],KEYS[3])\n" + // 锁超时时间的设置
            "local c = redis.call('HGET',KEYS[1],'count')\n" +  // 获取设置资源keys[1]现在被加了多少把锁
            "if type(c) ~= 'string' or tonumber(c) < 0 then\n" +  // 获取设置资源keys[1]现在背加的锁数量小于0
            "    redis.call('HSET',KEYS[1],'count',1)\n" +  // 设置资源keys[1]现在背加的锁数量为1
            "else \n" +
            "-- 如果是重入，记录获取次数\n" +
            "    redis.call('HSET',KEYS[1],'count',c+1)\n" + // 设置资源keys[1]现在背加的锁数量比之前多一个
            "end\n" +
            "return 1";

    static final String redisLockKey  = "lock_resource"; // 要锁的资源

    /**
     * 获得锁
     *
     * @param jedis        redis 连接
     * @param expireSecond 持有锁超时秒数
     * @param whoTryLock         线程标识   谁在申请锁
     * @return
     */
    public  static boolean tryLockInner(Jedis jedis, int expireSecond, String whoTryLock) {
        // 尝试获得锁 如果自身持有锁则可以再次获得
        if ((Long) jedis.eval(LOCK_SCRIPT, 3, redisLockKey, whoTryLock, "" + expireSecond) > 0) {  // 大于0 加锁成功  等于0 加锁失败
            return true;
        }
        return false;
//        // 阻塞等待释放锁通知 阻塞可能会有性能问题
//        //此处是在等待获取锁的list列表(存的是哪些人要获取资源的锁)里面取一个要加锁的人，后面再尝试加锁
//        List<String> lp = jedis.blpop(waitSecond, redisListKey);  // waitSecond   等待锁超时秒数
//        if (lp == null || lp.size() < 1) {
//            // 如果超时则返回锁定失败
//            return false;
//        }
//        return tryLockInner(jedis, expireSecond, waitSecond, flag);
    }

//    作者：zsr251
//    链接：https://hacpai.com/article/1513007414553
//    来源：黑客派
//    协议：CC BY-SA 4.0 https://creativecommons.org/licenses/by-sa/4.0/

    /**
     * 释放锁 lua 脚本
     * 两个参数：key、线程标识
     */
    public static String UNLOCK_SCRIPT = "local f = redis.call('HGET',KEYS[1],'flag')\n" +  //获得持有锁的人，加锁的资源是keys[1]
            "-- 如果线程标识不是空 且不是当前线程 则返回解锁失败\n" +
            "if type(f) ~= 'string' or (type(f) == 'string' and f ~= KEYS[2]) then \n" +// keys[2] 申请锁的人， f是redis里面现在持有锁的人
            "    return 0\n" +      // 不是同一人，返回0 解锁失败
            "end\n" +
            "local c = redis.call('HGET',KEYS[1],'count')\n" + // 获取资源keys[1]现在 加的锁数量
            "if type(c) ~= 'string' or tonumber(c) < 2 then\n" + // 锁数量小于2个，1个或0个，或小于0  直接删除key，解锁
            "    redis.call('DEL',KEYS[1])\n" +
            "    -- 释放成功 不再持有\n" +
            "    return 1    \n" +
            "else\n" +
            "    redis.call('HSET',KEYS[1],'count',c-1)\n" +    // 锁数量超过1个，将锁的数量减1，次数任然有锁
            "    -- 释放成功 但依然持有 即同一个线程多次获得锁的情况\n" +
            "    return 2\n" +
            "end";
    /**
     * 释放锁
     *
     * @param whoTryUnLock 线程标识  相当于谁在尝试解锁
     * @return
     */
    public  static  boolean tryUnlock(Jedis jedis, String whoTryUnLock) {
        try {
            // 删除锁定的 key
            Long l = (Long) jedis.eval(UNLOCK_SCRIPT, 2, redisLockKey, whoTryUnLock);  // 0 解锁失败 1 释放成功 不再持有锁  2 释放成功 但依然持有锁
            if (l < 1) {
                return false;
            }
            // 因为是可重入锁 所以释放成功不一定会释放锁
            if (l.intValue() == 2) {
                return true;
            }
//            // 如果锁释放消息队列里没有值 则释放一个信号
//            if (l.intValue() == 1 && jedis.llen(redisListKey).intValue() == 0) {
//                // 通知等待的线程可以继续获得锁
//                jedis.rpush(redisListKey, "ok");
//            }
            return true;
        } finally {
            jedis.close();
        }
    }

//    作者：zsr251
//    链接：https://hacpai.com/article/1513007414553
//    来源：黑客派
//    协议：CC BY-SA 4.0 https://creativecommons.org/licenses/by-sa/4.0/
}
