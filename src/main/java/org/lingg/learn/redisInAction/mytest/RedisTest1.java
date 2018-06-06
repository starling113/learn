package org.lingg.learn.redisInAction.mytest;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lingg.learn.redisInAction.book.RedisConst;
import org.springframework.data.redis.core.RedisCommand;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import sun.java2d.pipe.AAShapePipe;

import java.lang.reflect.Method;
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


    @Test
    public void testReflect() throws NoSuchMethodException {
        Jedis conn = new Jedis(RedisConst.redisHost);
        conn.select(RedisConst.redisDbIndex);

        Transaction trans = conn.multi();
//        Method method = trans.getClass().getDeclaredMethod("close", null);
//
//        System.out.println(method);

        System.out.println(conn.multi());

//        Method[] methods = trans.getClass().get
//        for(Method m : methods){
//            System.out.println(m);
//        }


//        // 将给定多个单词对应的集合进行交集计算，将计算结果存储到一个临时集合中
//        trans.getClass()
//                .getDeclaredMethod(method, new Class[]{String.class, String[].class})
//                .invoke(trans, "idx:" + id, keys); // sinterstore  命令 将keys所有集合元素保存到 idx：id中

    }


    @Test
    public void testSet(){

        Jedis conn = new Jedis(RedisConst.redisHost);
        conn.select(RedisConst.redisDbIndex);


        //String set(String key, String value, String nxxx, String expx, long time);

//        String result = this.jedis.set(LOCK_PREFIX + key, request, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, 10* TIME);
//        if (LOCK_MSG.equals(result)){
//            return true ;
//        }else {
//            return false ;
//        }

        System.out.println(conn.set("name", "jack"));
        System.out.println(conn.set("name", "jack", "NX"));
        System.out.println(conn.set("name", "jack", "","",23));
        //System.out.println(conn.set("name", "jack"));

        conn.close();
    }

    @Test
    public void test2(){
        // 返回 hashcode ，无论有没有重新hashcode方法，均与默认的方法 hashCode() 返回的代码一样
        System.out.println(System.identityHashCode("ss"));
        System.out.println("ss".hashCode());
    }

    @Test
    public void testEvalAndEvalsha(){
        Jedis conn = new Jedis(RedisConst.redisHost);
        conn.select(RedisConst.redisDbIndex);

        //eval
        String scripts = "return redis.call('setnx', KEYS[1], ARGV[1])";
        conn.eval(scripts, 1, "aa", "bb");
        System.out.println(conn.get("aa"));


        //evalsha
        String sha1Hex = DigestUtils.sha1Hex(scripts);
        conn.evalsha(sha1Hex, 1, "nn", "mm");
        System.out.println(conn.get("nn"));


        conn.close();
    }

}
