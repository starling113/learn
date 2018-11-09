package org.lingg.spring.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestCache {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-cache.xml");
        TestCacheService testCache = (TestCacheService)context.getBean("testCache");
//        System.out.println(testCache.get("jobs"));
//        System.out.println(testCache.get("jobs"));

//        System.out.println(testCache.get2());
//        System.out.println(testCache.get2());




        CacheManager cm =  (CacheManager) context.getBean("cacheManager");

//        //添加数据到缓存中
//        testCache.get("job");
        Cache uCache = cm.getCache("uCache");
//        //通过参数作为key，得到对应的value
//        User u1 = (User) uCache.get("job").get();
//        u1.show();

        //添加数据到缓存中
        testCache.get2();
        //通过方法名作为key，得到对应value
        User u2 = (User) uCache.get("get2").get();
        u2.show();

      }    
}