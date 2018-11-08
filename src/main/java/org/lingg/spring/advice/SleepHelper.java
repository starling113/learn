package org.lingg.spring.advice;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

//定义通知
public class SleepHelper implements MethodBeforeAdvice,AfterReturningAdvice {
    @Override
    public void before(Method arg0, Object[] arg1, Object arg2)
            throws Throwable {
        System.out.println("睡觉前要脱衣服！");
    }

    @Override
    public void afterReturning(Object arg0, Method arg1, Object[] arg2,
            Object arg3) throws Throwable {
        System.out.println("起床后要穿衣服！");
    }
}