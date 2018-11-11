package org.lingg.jdk.thread;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.*;

public class ThreadPoolExecutorTest {
    @Test
    public void test1() throws Exception  {

        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            try {
                Thread.sleep(index * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cachedThreadPool.execute(new Runnable() {
                public void run() {
                    System.out.println(Thread.currentThread().getName()+" "+LocalDateTime.now() + " "+index);
                }
            });
        }

        Thread.currentThread().join(); // 等待线程结束
    }

    @Test
    public void test2() throws Exception  {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            fixedThreadPool.execute(new Runnable() {
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName()+" "+LocalDateTime.now() + " "+index);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        Thread.currentThread().join(); // 等待线程结束
    }

    @Test
    public void test3() throws Exception  {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            singleThreadExecutor.execute(new Runnable() {
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName()+" "+LocalDateTime.now() + " "+index);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        Thread.currentThread().join(); // 等待线程结束
    }

    @Test
    public void test4() throws Exception {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        scheduledThreadPool.schedule(new Runnable() {
            public void run() {
                System.out.println("delay 3 seconds");
            }
        }, 3, TimeUnit.SECONDS);


        Thread.currentThread().join(); // 等待线程结束
    }

    public static void main(String[] args) {
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(2);


        final Runnable beeper = new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName()+" "+LocalDateTime.now() + " beep");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleWithFixedDelay(beeper, 3, 3, TimeUnit.SECONDS);

        scheduler.schedule(new Runnable() {
            public void run() {
                beeperHandle.cancel(true);
                System.out.println(Thread.currentThread().getName()+" "+LocalDateTime.now() + " stop beep");
            }
        }, 30, TimeUnit.SECONDS);

    }


}