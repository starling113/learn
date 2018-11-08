package org.lingg.spring;

public class Human implements Sleepable {

    @Override
    public void sleep() {
        System.out.println("我要睡觉了！");
    }
}