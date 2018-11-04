package org.lingg.learn.redisInAction.mytest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.lingg.learn.redisInAction.book.RedisConst;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.util.concurrent.*;

@Slf4j
public class RedisPipelineTest {


    private static String host = "192.168.181.129";
    private static int port = 6379;

    public static void main(String[] args) throws Exception {
        Jedis conn = new Jedis(host);
        conn.select(12);

        testPipeLineAndNormal(conn);
    }



    /*
     * 测试普通模式与PipeLine模式的效率：
     * 测试方法：向redis中插入10000组数据
     */
    public static void testPipeLineAndNormal(Jedis jedis)
            throws InterruptedException {
//        Logger logger = Logger.getLogger("javasoft");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            jedis.set(String.valueOf(i), String.valueOf(i));
        }
        long end = System.currentTimeMillis();
        log.info("普通模式：the jedis total time is:" + (end - start)+"毫秒");

        Pipeline pipe = jedis.pipelined();// 先创建一个pipeline的链接对象
        long start_pipe = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            pipe.set(String.valueOf(i), String.valueOf(i));
        }
        pipe.sync();// 获取所有的response
        long end_pipe = System.currentTimeMillis();
        log.info("pipeline模式：the pipe total time is:" + (end_pipe - start_pipe)+"毫秒");

        BlockingQueue<String> logQueue = new LinkedBlockingQueue<String>();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            logQueue.put("i=" + i);
        }
        long stop = System.currentTimeMillis();
        log.info("阻塞队列：the BlockingQueue total time is:" + (stop - begin)+"毫秒");
    }
//    原文：https://blog.csdn.net/u011489043/article/details/78769428
}
