package org.lingg.spring;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestAOP {


    @Test
    public void methodAdvisor() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-advisor.xml");
        Sleepable sleeper = (Sleepable) context.getBean("human");
        sleeper.sleep();
    }

    @Test
    public void methodAspect() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-aspect.xml");
        Sleepable sleeper = (Sleepable) context.getBean("human");
        sleeper.sleep();
    }

//执行结果
//睡觉前要脱衣服！
//我要睡觉了！
//起床后要穿衣服！
}