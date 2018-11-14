package org.lingg.mymap;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class Demo2 {



    /**
     * Callalbe和Runnable的区别
     * 
     * Runnable run方法是被线程调用的，在run方法是异步执行的
     * 
     * Callable的call方法，不是异步执行的，是由Future的run方法调用的
     * 
     * 
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {


        FutureTask<Integer> task = new FutureTask<>(()-> {

            System.out.println("正在计算结果...");
            Thread.sleep(3000);
            return 1;

        });

        new Thread(task).start();


        // do something
        System.out.println(" 干点别的...");

        Integer result = task.get(50,TimeUnit.SECONDS);

        System.out.println("拿到的结果为：" + result);

    }

}