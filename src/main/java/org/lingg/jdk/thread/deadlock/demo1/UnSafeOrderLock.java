package org.lingg.jdk.thread.deadlock.demo1;

import javax.naming.InsufficientResourcesException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 死锁版本
public class UnSafeOrderLock {
    private static final Object tieLock = new Object();

    public void transferMoney(final Account fromAcct, final Account toAcct, final int amount)
            throws InsufficientResourcesException {

        class Helper {
            public void transfer() throws InsufficientResourcesException {
                if (fromAcct.get() < amount)
                    throw new InsufficientResourcesException();
                else {
                    fromAcct.debit(amount);
                    toAcct.credit(amount);
                }
            }
        }

        // 两个用户使用这两个账户给对方转账时，死锁；因为一方fromAcct账户为对方的toAcct账户
        synchronized (fromAcct) {
            synchronized (toAcct) {
                new Helper().transfer();
            }
        }
    }

    class MyThread implements Runnable {
        private Account fromAcct;
        private Account toAcct;
        private int amount;

        public MyThread(Account fromAcct, Account toAcct, int amount) {
            this.fromAcct = fromAcct;
            this.toAcct = toAcct;
            this.amount = amount;
        }


        @Override
        public void run() {
            try {
                transferMoney(this.fromAcct, this.toAcct, this.amount);
            } catch (InsufficientResourcesException e) {
                System.out.println("操作失败");
            }
        }

    }

    public static void main(String[] args) {
        Account fromAcct = new Account(100);
        Account toAcct = new Account(230);
        UnSafeOrderLock orderLock = new UnSafeOrderLock();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            if ((i & 1) == 0) {
                threadPool.execute(orderLock.new MyThread(fromAcct, toAcct, 10));
            } else {
                threadPool.execute(orderLock.new MyThread(toAcct, fromAcct, 10));
            }
        }

        //注：转账双发的账户放生了反转，Jack的toAcct变成了Bob的fromAcct

        // 死锁的四个必要条件

        //互斥使用（资源独占）
        //一个资源每次只能给一个进程使用
        //
        //不可抢占（不可剥夺）
        //资源申请者不能强行的从资源占有者手中夺取资源，资源 只能由占有者自愿释放
        //
        //请求和保持（部分分配，占有申请）
        //一个进程在申请新的资源的同时保持对原有资源的占有（只要这样才是动态申请，动态分配）
        //
        //循环等待
        //存在一个进程等待队列{P1, P2, … ，Pn}，其中P1等待P2占有的资源，P2等待P3占有的资源，…，Pn等待P1占有的资源，星辰给一个进程等待环路


//        threadPool.execute(orderLock.new MyThread(fromAcct, toAcct, 10));
//        threadPool.execute(orderLock.new MyThread(toAcct, fromAcct, 10));
    }
}
//---------------------
//作者：renwotao2009
//来源：CSDN
//原文：https://blog.csdn.net/renwotao2009/article/details/51083396
//版权声明：本文为博主原创文章，转载请附上博文链接！