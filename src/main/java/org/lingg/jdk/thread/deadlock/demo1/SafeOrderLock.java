package org.lingg.jdk.thread.deadlock.demo1;

import javax.naming.InsufficientResourcesException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//// 解决死锁版本
public class SafeOrderLock {
    private static final Object tieLock = new Object();

    public void transferMoney(final Account fromAcct, final Account toAcct, final int amount)  throws InsufficientResourcesException {
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

        // 转账双方共用这两个账户的对象，否则无法通过下面方式排序下面的锁顺序
        int fromHash = System.identityHashCode(fromAcct);
        int toHash = System.identityHashCode(toAcct);

        //**解决静态的锁顺序死锁的方法就是：所有需要多个锁的线程，都要以相同的顺序来获得锁。 **
        if (fromHash < toHash) { //理解：嵌套的synchronized是按照嵌套的顺序获取锁，即先获取最外层的锁fromAcct，再获取toAcct的锁。
            synchronized (fromAcct) {
                synchronized (toAcct) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAcct) {
                synchronized (fromAcct) {
                    new Helper().transfer();
                }   
            }
        } else {
            synchronized (tieLock) {
                synchronized (fromAcct) {
                    synchronized (toAcct) {
                        new Helper().transfer();
                    }
                }
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
        // 转账双方共用这两个账户对象
        Account fromAcct = new Account(100);
        Account toAcct = new Account(230);
        SafeOrderLock orderLock = new SafeOrderLock();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            if ((i & 1) == 0)
                threadPool.execute(orderLock.new MyThread(fromAcct, toAcct, 10));
            // 注：转账的账户变成了toAcct，被转账的账户变成了fromAcct
            else threadPool.execute(orderLock.new MyThread(toAcct, fromAcct, 10));
        }
    }
}
//---------------------
//作者：renwotao2009
//来源：CSDN
//原文：https://blog.csdn.net/renwotao2009/article/details/51083396
//版权声明：本文为博主原创文章，转载请附上博文链接！