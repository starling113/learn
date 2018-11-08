package org.lingg.spring.apect;

//定义切面
public class SleepHelperAspect{
    public void beforeSleep(){
        System.out.println("睡觉前要脱衣服！");
    }

    public void afterSleep(){
        System.out.println("起床后要穿衣服！");
    }
}