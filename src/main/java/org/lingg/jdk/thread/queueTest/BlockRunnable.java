package org.lingg.jdk.thread.queueTest;

public class BlockRunnable implements Runnable {
    private final String mName;

    public BlockRunnable(String name) {
        mName = name;
    }

    public void run() {
        System.out.println(String.format("[%s] %s 执行", Thread.currentThread().getName(), mName));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}