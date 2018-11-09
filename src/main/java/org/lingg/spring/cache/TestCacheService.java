package org.lingg.spring.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("testCache")
public class TestCacheService {

    //通过参数name
    @Cacheable(cacheNames="uCache",key="#name")
//    @Cacheable(value = "userCache")
    public User get(String name){

        User u = new User();
        u.setAge(12);
        u.setUserName("jobs");
        System.out.println("没有缓存,从数据库查出结果："+name);
        return u;
    }

    //通过方法名
    @Cacheable(value="uCache",key="#root.methodName")
    public User get2(){
        User u = new User();
        u.setAge(12);
        u.setUserName("gate");
        System.out.println("没有缓存");
        return u;
    }
}