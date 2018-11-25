package org.lingg.jdk.thread.queueTest;

import org.junit.jupiter.api.Test;
import scala.reflect.internal.Trees;

import java.util.concurrent.*;

public class QueueTest {

    public static void main(String[] args) {

        ExecutorService executor =  new ThreadPoolExecutor(2, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(4));

//        拒绝策略
//        AbortPolicy:丢弃任务并抛出RejectedExecutionException
//
//        CallerRunsPolicy：只要线程池未关闭，该策略直接在调用者线程中，运行当前被丢弃的任务。显然这样做不会真的丢弃任务，但是，任务提交线程的性能极有可能会急剧下降。
//
//        DiscardOldestPolicy：丢弃队列中最老的一个请求，也就是即将被执行的一个任务，并尝试再次提交当前任务。
//
//        DiscardPolicy：丢弃任务，不做任何处理。
        RejectedExecutionHandler rejectedExecutionHandler = ((ThreadPoolExecutor) executor).getRejectedExecutionHandler();
        System.err.println(rejectedExecutionHandler);//java.util.concurrent.ThreadPoolExecutor$AbortPolicy@5f184fc6
        ((ThreadPoolExecutor) executor).setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        for (int i = 0; i < 10; i++) {
            executor.execute(new BlockRunnable(String.valueOf(i)));
        }
    }

    CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void test1() throws Exception{

//        ExecutorService executor =  new ThreadPoolExecutor(2, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        ExecutorService executor =  new ThreadPoolExecutor(2, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        for (int i = 0; i < 10; i++) {
            executor.execute(new BlockRunnable(String.valueOf(i)));
        }

        //Thread.currentThread().join();


//        latch.await();//加入该代码，让主线程不挂掉
        System.in.read();
    }

// http://www.cnblogs.com/dolphin0520/p/3932921.html
//    如果当前线程池中的线程数目小于corePoolSize，则每来一个任务，就会创建一个线程去执行这个任务；
//    如果当前线程池中的线程数目>=corePoolSize，则每来一个任务，会尝试将其添加到任务缓存队列当中，若添加成功，则该任务会等待空闲线程将其取出去执行；若添加失败（一般来说是任务缓存队列已满），则会尝试创建新的线程去执行这个任务；
//    如果当前线程池中的线程数目达到maximumPoolSize，则会采取任务拒绝策略进行处理；
//    如果线程池中的线程数量大于 corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止，直至线程池中的线程数目不大于corePoolSize；如果允许为核心池中的线程设置存活时间，那么核心池中的线程空闲时间超过keepAliveTime，线程也会被终止。

    @Test
    public void test2() throws Exception{
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(5));

        for(int i=0;i<15;i++){
            BlockRunnable myTask = new BlockRunnable(i+"");
            executor.execute(myTask);
            System.out.println("线程池中线程数目："+executor.getPoolSize()+"，队列中等待执行的任务数目："+
                    executor.getQueue().size()+"，已执行玩别的任务数目："+executor.getCompletedTaskCount());
        }
        executor.shutdown();

        System.in.read();

        Executors.newFixedThreadPool(5);
    }
}
